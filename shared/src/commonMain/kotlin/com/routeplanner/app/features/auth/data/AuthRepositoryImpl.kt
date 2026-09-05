package com.routeplanner.app.features.auth.data

import com.routeplanner.app.core.common.domain.DomainError
import com.routeplanner.app.features.auth.data.dto.toDomain
import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.auth.data.remote.AuthApi
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.errors.IOException
import com.routeplanner.app.core.common.domain.Result
import com.routeplanner.app.features.auth.data.dto.ApiResponse
import com.routeplanner.app.features.auth.domain.model.Session
import com.routeplanner.app.features.auth.domain.repository.AuthRepository
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

/**
 * Mapeo de errores asumido (avisame si tu backend distingue distinto):
 *  - Sin conexión / timeout / DNS               -> DomainError.Network
 *  - 401 / 403 (credenciales inválidas)         -> DomainError.InvalidCredentials
 *  - Otros 4xx con body { success:false, message } -> DomainError.ServerMessage
 *  - 5xx                                        -> DomainError.Server
 *  - "success": false con 200 (por si tu backend responde así) -> DomainError.ServerMessage
 *
 * Esto asume que el HttpClient tiene expectSuccess = true, por eso los 4xx/5xx
 * llegan como excepciones (ClientRequestException / ServerResponseException) y
 * no como una respuesta normal.
 */
class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Session> {
        return try {
            val response = authApi.login(username, password)
            if (response.success && response.data != null) {
                println("Login response: $response")
                val session = response.data.toDomain()
                sessionLocalDataSource.saveSession(session)
                Result.Success(session)
            } else {
                Result.Error(DomainError.ServerMessage(response.message ?: "No se pudo iniciar sesión"))
            }
        } catch (e: ClientRequestException) {
            mapClientError(e)
        } catch (e: ServerResponseException) {
            Result.Error(DomainError.Server)
        } catch (e: IOException) {
            Result.Error(DomainError.Network)
        } catch (e: SerializationException) {
            Result.Error(DomainError.Server)
        } catch (e: Exception) {
            Result.Error(DomainError.Server)
        }
    }

    private suspend fun mapClientError(e: ClientRequestException): Result<Session> {
        if (e.response.status == HttpStatusCode.Unauthorized ||
            e.response.status == HttpStatusCode.Forbidden
        ) {
            return Result.Error(DomainError.InvalidCredentials)
        }

        val message = runCatching {
            json.decodeFromString<ApiResponse<Unit>>(
                e.response.bodyAsText()
            ).message
        }.getOrNull()

        return Result.Error(DomainError.ServerMessage(message ?: "Ocurrió un error, intenta nuevamente"))
    }

    override fun getSession(): Session? = sessionLocalDataSource.getSession()

    override fun isLoggedIn(): Boolean = sessionLocalDataSource.isLoggedIn()

    override fun logout() = sessionLocalDataSource.clearSession()
}

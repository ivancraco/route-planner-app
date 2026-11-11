package com.routeplanner.app.features.auth.data

import com.routeplanner.app.core.common.domain.DomainError
import com.routeplanner.app.features.auth.data.dto.toDomain
import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.auth.data.remote.AuthApi
import com.routeplanner.app.core.common.domain.Result
import com.routeplanner.app.features.auth.domain.model.Session
import com.routeplanner.app.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionLocalDataSource: SessionLocalDataSource
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<Session> {
        return try {
            val response = authApi.login(username, password)
            if (response.success && response.data != null) {
                val session = response.data.toDomain()
                sessionLocalDataSource.saveSession(session)
                Result.Success(session)
            } else {
                Result.Error(DomainError.ServerMessage(response.error?.message ?: "No se pudo iniciar sesión."))
            }
        } catch (_: Exception) {
            Result.Error(DomainError.ServerMessage("Ocurrió un error. Intente nuevamente."))
        }
    }

    override fun getSession(): Session? = sessionLocalDataSource.getSession()

    override fun isLoggedIn(): Boolean = sessionLocalDataSource.isLoggedIn()

    override fun logout() = sessionLocalDataSource.clearSession()
}

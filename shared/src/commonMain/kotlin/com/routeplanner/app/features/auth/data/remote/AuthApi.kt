package com.routeplanner.app.features.auth.data.remote

import com.routeplanner.app.core.di.BASE_URL
import com.routeplanner.app.features.auth.data.dto.ApiResponse
import com.routeplanner.app.features.auth.data.dto.LoginRequestDto
import com.routeplanner.app.features.auth.data.dto.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Ajustá la ruta "auth/login" a la real de tu backend.
 * Se asume que el HttpClient inyectado ya tiene configurado:
 *  - baseUrl (o url completa acá)
 *  - ContentNegotiation con kotlinx.serialization
 *  - expectSuccess = true (para que Ktor lance ClientRequestException /
 *    ServerResponseException en respuestas 4xx/5xx en vez de devolverlas
 *    "silenciosamente"). Si tu NetworkModule usa expectSuccess = false,
 *    avisame y adapto el manejo en AuthRepositoryImpl.
 */
class AuthApi(
    private val httpClient: HttpClient
) {
    suspend fun login(username: String, password: String): ApiResponse<LoginResponseDto> {
        return httpClient.post(BASE_URL + "users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username = username, password = password))
        }.body()
    }
}

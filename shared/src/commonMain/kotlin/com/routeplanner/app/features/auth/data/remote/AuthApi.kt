package com.routeplanner.app.features.auth.data.remote

import com.routeplanner.app.features.auth.data.dto.ApiResponse
import com.routeplanner.app.features.auth.data.dto.LoginRequestDto
import com.routeplanner.app.features.auth.data.dto.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(
    private val httpClient: HttpClient
) {
    suspend fun login(username: String, password: String): ApiResponse<LoginResponseDto> {
        return httpClient.post("users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequestDto(username = username, password = password))
        }.body()
    }
}

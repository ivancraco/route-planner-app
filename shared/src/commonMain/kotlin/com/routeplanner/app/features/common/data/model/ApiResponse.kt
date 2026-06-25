package com.routeplanner.app.features.common.data.model

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiError(
    val code: Int,
    val message: String
)

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(
        val code: Int,
        val message: String
    ) : ApiResult<Nothing>
}

suspend inline fun <reified T> HttpResponse.parseApiResponse(): ApiResult<T> {
    val response: ApiResponse<T> = body()

    return if (response.success) {
        ApiResult.Success(
            requireNotNull(response.data)
        )
    } else {
        ApiResult.Error(
            code = response.error?.code ?: -1,
            message = response.error?.message ?: "Error desconocido"
        )
    }
}
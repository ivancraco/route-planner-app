package com.routeplanner.app.features.auth.data.dto

import kotlinx.serialization.Serializable

/**
 * Envelope genérico que devuelve tu backend:
 * { "success": true, "data": {...} } o { "success": false, "message": "..." }
 *
 * Si en tu backend el campo de error se llama distinto (p. ej. "error" en vez de
 * "message"), ajustá el @SerialName acá.
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)

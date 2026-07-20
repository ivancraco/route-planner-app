package com.routeplanner.app.features.home.places

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PlacesSessionTokenProvider {
    private var currentToken: String? = null

    fun current(): String = currentToken ?: newToken()

    private fun newToken(): String = Uuid.random().toString().also { currentToken = it }

    /** Llamar después de un Place Details exitoso: la sesión se considera cerrada. */
    fun reset() {
        currentToken = null
    }
}
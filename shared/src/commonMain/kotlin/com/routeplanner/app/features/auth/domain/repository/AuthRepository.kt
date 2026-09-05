package com.routeplanner.app.features.auth.domain.repository

import com.routeplanner.app.core.common.domain.Result
import com.routeplanner.app.features.auth.domain.model.Session

interface AuthRepository {

    suspend fun login(username: String, password: String): Result<Session>

    /** Sesión guardada localmente, o null si no hay ninguna (usuario no logueado). */
    fun getSession(): Session?

    fun isLoggedIn(): Boolean

    fun logout()
}

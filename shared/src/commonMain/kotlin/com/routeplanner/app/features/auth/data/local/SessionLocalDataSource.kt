package com.routeplanner.app.features.auth.data.local

import com.routeplanner.app.features.auth.domain.model.Session
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get

/**
 * Guarda y recupera la sesión del usuario usando multiplatform-settings.
 * Se asume que la instancia de [Settings] ya está provista en tu Koin (core).
 * Si querés separarla en su propio "namespace" (para no mezclar con otras
 * preferencias) podés inyectar acá una Settings creada con
 * Settings.Factory().create("session_settings") en vez de la global.
 */
class SessionLocalDataSource(
    private val settings: Settings
) {

    fun saveSession(session: Session) {
        settings.putInt(KEY_ID, session.id)
        settings.putString(KEY_USERNAME, session.username)
        settings.putBoolean(KEY_IS_SUPERVISOR, session.isSupervisor)
        settings.putString(KEY_ACCESS_TOKEN, session.accessToken)
        settings.putString(KEY_REFRESH_TOKEN, session.refreshToken)
    }

    fun getSession(): Session? {
        val accessToken = settings.getStringOrNull(KEY_ACCESS_TOKEN)
        val refreshToken = settings.getStringOrNull(KEY_REFRESH_TOKEN)
        val username = settings.getStringOrNull(KEY_USERNAME)

        if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank() || username.isNullOrBlank()) {
            return null
        }

        return Session(
            id = settings.getInt(KEY_ID, -1),
            username = username,
            isSupervisor = settings.getBoolean(KEY_IS_SUPERVISOR, false),
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun getAccessToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)

    fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)

    fun isLoggedIn(): Boolean = settings.hasKey(KEY_ACCESS_TOKEN)

    fun clearSession() {
        settings.remove(KEY_ID)
        settings.remove(KEY_USERNAME)
        settings.remove(KEY_IS_SUPERVISOR)
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    private companion object {
        const val KEY_ID = "session_id"
        const val KEY_USERNAME = "session_username"
        const val KEY_IS_SUPERVISOR = "session_is_supervisor"
        const val KEY_ACCESS_TOKEN = "session_access_token"
        const val KEY_REFRESH_TOKEN = "session_refresh_token"
    }
}

private fun Settings.getStringOrNull(key: String): String? =
    if (hasKey(key)) get<String>(key) else null

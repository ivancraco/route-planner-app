package com.routeplanner.app.features.auth.presentation

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isLoginEnabled: Boolean
        get() = username.isNotBlank() && password.isNotBlank() && !isLoading
}

sealed interface AuthEvent {
    data class UsernameChanged(val value: String) : AuthEvent
    data class PasswordChanged(val value: String) : AuthEvent
    data object LoginClicked : AuthEvent
    data object ErrorMessageShown : AuthEvent
}

sealed interface AuthEffect {
    data class NavigateAfterLogin(val isSupervisor: Boolean) : AuthEffect
}

package com.routeplanner.app.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routeplanner.app.core.common.domain.DomainError
import com.routeplanner.app.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.routeplanner.app.core.common.domain.Result

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effect = Channel<AuthEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.UsernameChanged ->
                _uiState.update { it.copy(username = event.value, errorMessage = null) }

            is AuthEvent.PasswordChanged ->
                _uiState.update { it.copy(password = event.value, errorMessage = null) }

            AuthEvent.LoginClicked -> login()

            AuthEvent.ErrorMessageShown ->
                _uiState.update { it.copy(errorMessage = null) }
        }
    }

    private fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu usuario y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = authRepository.login(state.username.trim(), state.password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _effect.send(AuthEffect.NavigateAfterLogin(result.data.isSupervisor))
                }

                is Result.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.error.toUserMessage())
                    }
                }
            }
        }
    }
}

private fun DomainError.toUserMessage(): String = when (this) {
    is DomainError.ServerMessage -> message
    DomainError.Network -> "No hay conexión a internet. Verifica tu red e intenta de nuevo."
    DomainError.InvalidCredentials -> "Usuario o contraseña incorrectos."
    DomainError.Server -> "Ocurrió un error en el servidor. Intenta más tarde."
}

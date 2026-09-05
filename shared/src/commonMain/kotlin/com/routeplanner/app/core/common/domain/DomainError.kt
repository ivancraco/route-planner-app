package com.routeplanner.app.core.common.domain

sealed interface DomainError {
    data class ServerMessage(val message: String) : DomainError
    data object Network : DomainError
    data object InvalidCredentials : DomainError
    data object Server : DomainError
}
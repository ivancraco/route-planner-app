package com.routeplanner.app.core.common.domain

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val error: DomainError) : Result<Nothing>
}
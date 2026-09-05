package com.routeplanner.app.features.home.presentation

import com.routeplanner.app.features.home.domain.model.UserStop

data class StopDetailState(
    val stop: UserStop? = null,
    val isEditing: Boolean = false,
    val noteInput: String = "",
    val isLoading: Boolean = false,
)

package com.routeplanner.app.core.common.domain.model

import com.routeplanner.app.features.home.domain.model.UserRoute

data class UIState(
    val data: List<UserRoute> = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = true,
)

sealed class UIRoute {
    data class Success(val data: List<UserRoute>) : UIRoute()
}

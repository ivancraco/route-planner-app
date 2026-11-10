package com.routeplanner.app.features.home.presentation

import com.routeplanner.app.features.home.domain.model.Notices
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum

data class StopFormState(
    val direction: String = "",
    val directionDetail: String = "",
    val directionPlaceId: String? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val recipient: String = "",
    val notice: Notices? = null,
    val note: String = "",
    val isSearchingDirection: Boolean = false,
    val isFillingForm: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

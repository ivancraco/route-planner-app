package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateStopDto(
    val stateId: Int? = null,
    val noticeId: Int? = null,
    val recipientName: String? = null,
    val direction: String? = null,
    val directionPlaceId: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val order: Int? = null,
    val note: String? = null,
)

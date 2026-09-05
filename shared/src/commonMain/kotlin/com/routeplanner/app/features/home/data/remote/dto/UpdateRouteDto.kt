package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRouteDto(
    val stateId: Int? = null,
    val name: String? = null,
    val originDir: String? = null,
    val originPlaceId: String? = null,
    val originLatitude: Double? = null,
    val originLongitude: Double? = null,
    val destinationDir: String? = null,
    val destinationPlaceId: String? = null,
    val destinationLatitude: Double? = null,
    val destinationLongitude: Double? = null
)
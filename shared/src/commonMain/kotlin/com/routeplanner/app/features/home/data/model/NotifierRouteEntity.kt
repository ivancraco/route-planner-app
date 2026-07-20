package com.routeplanner.app.features.home.data.model

import kotlin.time.Instant

data class NotifierRouteEntity(
    val userId: Long,
    val stateId: Long,
    val name: String,
    val createdAt: Instant,
    val originDir: String,
    val originPlaceId: String? = null,
    val originLatitude: Double,
    val originLongitude: Double,
    val destinationDir: String,
    val destinationPlaceId: String? = null,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
)
package com.routeplanner.app.features.notifier.domain.model

import kotlin.time.Instant

data class NotifierRoute(
    val id: Long,
    //val userId: Long,
    val state: String,
    val name: String,
    val createdAt: Instant,
    val originDir: String,
    val originPlaceId: String?,
    val originLatitude: Double,
    val originLongitude: Double,
    val destinationDir: String,
    val destinationPlaceId: String?,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val notifierStops: List<NotifierStop>,
)
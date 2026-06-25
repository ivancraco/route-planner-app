package com.routeplanner.app.features.notifier.domain.model

data class NotifierStop(
    val id: Long,
    val notice: String,
    val state: String,
    val recipient: String,
    val direction: String,
    val directionPlaceId: String?,
    val latitude: Double,
    val longitude: Double,
    val order: Long,
    val note: String?,
)
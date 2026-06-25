package com.routeplanner.app.features.notifier.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class NotifierRouteSync(
    @SerialName("id") val id: Long,
    @SerialName("state_id") val stateId: Long,
    @SerialName("name") val name: String,
    @SerialName("createdAt") val createdAt: Instant,
    @SerialName("originDir") val originDir: String,
    @SerialName("originPlaceId") val originPlaceId: String?,
    @SerialName("originLatitude") val originLatitude: Double,
    @SerialName("originLongitude") val originLongitude: Double,
    @SerialName("destinationDir") val destinationDir: String,
    @SerialName("destinationPlaceId") val destinationPlaceId: String?,
    @SerialName("destinationLatitude") val destinationLatitude: Double,
    @SerialName("destinationLongitude") val destinationLongitude: Double,
    @SerialName("stops") val stops: List<NotifierStopSync>,
    )

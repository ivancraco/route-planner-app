package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class CreateRouteDto(
    @SerialName("id") val id: String,
    @SerialName("stateId") val stateId: Int,
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
    @SerialName("stops") val stops: List<CreateStopDto> = listOf(),
    )

package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateStopDto(
    @SerialName("id") val id: String,
    @SerialName("noticeId") val noticeId: Int,
    @SerialName("stateId") val stateId: Int,
    @SerialName("recipient") val recipient: String,
    @SerialName("direction") val direction: String,
    @SerialName("directionPlaceId") val directionPlaceId: String?,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("order") val order: Int,
    @SerialName("note") val note: String?,
)

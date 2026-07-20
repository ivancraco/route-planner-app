package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotifierStopSync(
    @SerialName("id") val id: Long,
    @SerialName("noticeId") val noticeId: Long,
    @SerialName("stateId") val stateId: Long,
    @SerialName("recipient") val recipient: String,
    @SerialName("direction") val direction: String,
    @SerialName("directionPlaceId") val directionPlaceId: String?,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("order") val order: Long?,
    @SerialName("note") val note: String?,
)

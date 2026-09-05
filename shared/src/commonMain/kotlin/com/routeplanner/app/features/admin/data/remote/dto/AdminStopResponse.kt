package com.routeplanner.app.features.admin.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminStopResponse(
    @SerialName("id") val id: Long,
    @SerialName("notice") val notice: String,
    @SerialName("state") val state: String,
    @SerialName("recipient") val recipient: String,
    @SerialName("direction") val direction: String,
    @SerialName("order") val order: Long?,
    @SerialName("note") val note: String?,
)
package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StopStateDto(
    @SerialName("id") val id: Int,
    @SerialName("description") val description: String
)
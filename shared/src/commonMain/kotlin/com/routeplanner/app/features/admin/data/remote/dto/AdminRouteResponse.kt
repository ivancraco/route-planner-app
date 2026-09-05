package com.routeplanner.app.features.admin.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class AdminRouteResponse(
    @SerialName("id") val id: Long,
    @SerialName("ownerName") val ownerName: String,
    @SerialName("name") val name: String,
    @SerialName("state") val state: String,
    @SerialName("createdAt") val createdAt: Instant,
    @SerialName("originDir") val originDir: String,
    @SerialName("destinationDir") val destinationDir: String,
    @SerialName("stops") val stops: List<AdminStopResponse>,
)
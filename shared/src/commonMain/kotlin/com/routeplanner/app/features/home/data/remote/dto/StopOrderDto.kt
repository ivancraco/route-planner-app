package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StopOrderDto(
    val id: String,
    val order: Int
)

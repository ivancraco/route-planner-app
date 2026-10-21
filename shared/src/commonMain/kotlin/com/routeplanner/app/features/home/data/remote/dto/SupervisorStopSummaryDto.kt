package com.routeplanner.app.features.home.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SupervisorStopSummaryDto(
    val state: String,
    val direction: String,
    val order: Int? = null
)

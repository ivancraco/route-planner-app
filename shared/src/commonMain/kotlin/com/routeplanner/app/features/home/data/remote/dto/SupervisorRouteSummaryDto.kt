package com.routeplanner.app.features.home.data.remote.dto

import com.routeplanner.app.features.home.domain.model.SupervisorStopSummary
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class SupervisorRouteSummaryDto(
    val name: String,
    val user: String,
    val date: Instant,
    val origin: String,
    val destination: String,
    val state: String,
    val stops: List<SupervisorStopSummaryDto>
)

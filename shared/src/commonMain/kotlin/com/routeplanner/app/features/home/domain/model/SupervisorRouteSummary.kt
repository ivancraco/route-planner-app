package com.routeplanner.app.features.home.domain.model

import kotlin.time.Instant

data class SupervisorRouteSummary(
    val name: String,
    val user: String,
    val date: Instant,
    val origin: String,
    val destination: String,
    val state: String,
    val stops: List<SupervisorStopSummary>
)

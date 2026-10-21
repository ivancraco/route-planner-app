package com.routeplanner.app.features.home.domain.model

data class SupervisorStopSummary(
    val state: String,
    val direction: String,
    val order: Int? = null
)

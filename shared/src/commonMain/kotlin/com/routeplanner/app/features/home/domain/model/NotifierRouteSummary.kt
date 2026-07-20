package com.routeplanner.app.features.home.domain.model

import kotlin.time.Instant

data class NotifierRouteSummary(
    val id: Long,
    val name: String,
    val createdAt: Instant,
)

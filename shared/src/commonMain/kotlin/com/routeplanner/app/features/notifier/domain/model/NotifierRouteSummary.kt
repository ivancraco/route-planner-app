package com.routeplanner.app.features.notifier.domain.model

import kotlin.time.Instant

data class NotifierRouteSummary(
    val id: Long,
    val name: String,
    val createdAt: Instant,
)

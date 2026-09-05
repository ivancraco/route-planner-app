package com.routeplanner.app.features.admin.domain.model

import kotlin.time.Instant

data class AdminRoute(
    val id: Long,
    val ownerName: String,
    val name: String,
    val state: String,
    val createdAt: Instant,
    val originDir: String,
    val destinationDir: String,
    val stops: List<AdminStop>,
)

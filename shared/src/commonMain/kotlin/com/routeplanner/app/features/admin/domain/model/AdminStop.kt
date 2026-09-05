package com.routeplanner.app.features.admin.domain.model

data class AdminStop(
    val id: Long,
    val notice: String,
    val state: String,
    val recipient: String,
    val direction: String,
    val order: Long,
    val note: String?
)

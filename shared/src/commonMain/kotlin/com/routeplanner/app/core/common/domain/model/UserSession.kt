package com.routeplanner.app.core.common.domain.model

data class UserSession(
    val id: Int,
    val username: String,
    val fullName: String,
    val isSupervisor: Boolean,
)
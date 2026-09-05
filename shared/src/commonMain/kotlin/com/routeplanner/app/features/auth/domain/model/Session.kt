package com.routeplanner.app.features.auth.domain.model

data class Session(
    val id: Int,
    val username: String,
    val isSupervisor: Boolean,
    val accessToken: String,
    val refreshToken: String
)

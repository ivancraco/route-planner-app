package com.routeplanner.app.features.auth.data.dto

import com.routeplanner.app.features.auth.domain.model.Session
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val id: Int,
    val username: String,
    val isSupervisor: Boolean,
    val accessToken: String,
    val refreshToken: String
)

fun LoginResponseDto.toDomain(): Session = Session(
    id = id,
    username = username,
    isSupervisor = isSupervisor,
    accessToken = accessToken,
    refreshToken = refreshToken
)

package com.routeplanner.app.core.common.data.model

import com.routeplanner.app.core.common.domain.model.UserSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserApiItem(
    @SerialName("id") val id: Int,
    @SerialName("username") val username: String,
    @SerialName("fullName") val fullName: String,
    @SerialName("isSupervisor") val isSupervisor: Boolean,
)

fun UserApiItem.toUser() = UserSession(
    id = id,
    username = username,
    fullName = fullName,
    isSupervisor = isSupervisor,
)
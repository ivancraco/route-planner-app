package com.routeplanner.app.features.home.domain.model

import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateStopDto

enum class StopStateEnum(val id: Int, val description: String) {
    PENDIENTE(1, "PENDIENTE"),
    EXITOSA(2, "EXITOSA"),
    FALLIDA(3, "FALLIDA");

    companion object {
        fun fromName(name: String): StopStateEnum =
            entries.find { it.name == name }
                ?: throw IllegalArgumentException("Unknown route state: $name")

        fun fromId(id: Int): StopStateEnum =
            entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown route state id: $id")
    }
}

enum class StopNoticeEnum(val id: Int, val description: String) {
    AVISO(1, "AVISO"),
    NOTIFICACION(2, "NOTIFICACION"),
    INTIMACION(3, "INTIMACION"),
    CEDULA(4, "CEDULA"),
    PLIEGO(5, "PLIEGO");

    companion object {
        fun fromName(name: String): StopNoticeEnum =
            entries.find { it.name == name }
                ?: throw IllegalArgumentException("Unknown route state: $name")

        fun fromId(id: Int): StopNoticeEnum =
            entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown route state id: $id")
    }
}

data class UserStop(
    val id: String,
    val routeId: String,
    val notice: String,
    val state: String,
    val recipient: String,
    val direction: String,
    val directionPlaceId: String?,
    val latitude: Double,
    val longitude: Double,
    val order: Int,
    val note: String?,
)

fun UserStop.toCreateStopDto(): CreateStopDto {
    return CreateStopDto(
        id = id,
        noticeId = StopNoticeEnum.fromName(notice).id,
        stateId = StopStateEnum.fromName(state).id,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = order,
        note = note
    )
}

fun UserStop.toUpdateStopDto(): UpdateStopDto {
    return UpdateStopDto(
        noticeId = StopNoticeEnum.fromName(notice).id,
        stateId = StopStateEnum.fromName(state).id,
        recipientName = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = order,
        note = note
    )
}
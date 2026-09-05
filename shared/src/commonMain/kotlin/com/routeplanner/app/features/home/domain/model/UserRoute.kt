package com.routeplanner.app.features.home.domain.model

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateRouteDto
import kotlin.time.Instant

enum class RouteStateEnum(val id: Int, val description: String) {
    ACTIVE(1, "EN CURSO"),
    FINISHED(2, "FINALIZADA"),
    CANCELED(3, "CANCELADA");

    companion object {
        fun fromName(name: String): RouteStateEnum =
            entries.find { it.description == name }
                ?: throw IllegalArgumentException("Unknown route state: $name")

        fun fromId(id: Int): RouteStateEnum =
            entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown route state id: $id")
    }
}

data class UserRoute(
    val id: String,
    val state: String,
    val name: String,
    val createdAt: Instant,
    val originDir: String,
    val originPlaceId: String? = null,
    val originLatitude: Double,
    val originLongitude: Double,
    val destinationDir: String,
    val destinationPlaceId: String? = null,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val encodedPolyline: String? = null,
    val userStops: List<UserStop> = listOf(),
)

fun UserRoute.toCreateRouteDto(): CreateRouteDto {
    return CreateRouteDto(
        id = id,
        stateId = RouteStateEnum.fromName(state).id,
        name = name,
        createdAt = createdAt,
        originDir = originDir,
        originPlaceId = originPlaceId,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationDir = destinationDir,
        destinationPlaceId = destinationPlaceId,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
        stops = if (userStops.isEmpty()) listOf() else userStops.map { it.toCreateStopDto() }
    )
}

fun UserRoute.toUpdateRouteDto(): UpdateRouteDto =
    UpdateRouteDto(
        stateId = RouteStateEnum.fromName(state).id,
        name = name,
        originDir = originDir,
        originPlaceId = originPlaceId,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        destinationDir = destinationDir,
        destinationPlaceId = destinationPlaceId,
        destinationLatitude = destinationLatitude,
        destinationLongitude = destinationLongitude,
    )
package com.routeplanner.app.features.home.domain.model

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import kotlin.time.Instant

data class CreateRoute(
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
)

fun CreateRoute.toCreateRouteDto(): CreateRouteDto = CreateRouteDto(
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
)

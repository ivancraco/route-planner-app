package com.routeplanner.app.features.home.domain.model

import com.routeplanner.app.features.home.data.remote.dto.UpdateRouteDto

data class UpdateRoute(
    val id: String,
    val stateId: Int? = null,
    val name: String? = null,
    val originDir: String? = null,
    val originPlaceId: String? = null,
    val originLatitude: Double? = null,
    val originLongitude: Double? = null,
    val destinationDir: String? = null,
    val destinationPlaceId: String? = null,
    val destinationLatitude: Double? = null,
    val destinationLongitude: Double? = null
)

fun UpdateRoute.toCreateRouteDto(): UpdateRouteDto = UpdateRouteDto(
    stateId = stateId,
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

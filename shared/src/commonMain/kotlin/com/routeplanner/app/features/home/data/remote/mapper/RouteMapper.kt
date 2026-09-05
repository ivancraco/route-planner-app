package com.routeplanner.app.features.home.data.remote.mapper

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.RouteStateEnum

fun CreateRouteDto.toDomain(): UserRoute {
    return UserRoute(
        id = id,
        state = RouteStateEnum.fromId(stateId).description,
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
        userStops = stops.map { it.toDomain(id) }
    )
}
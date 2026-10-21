package com.routeplanner.app.features.home.data.remote.mapper

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.data.remote.dto.SupervisorRouteSummaryDto
import com.routeplanner.app.features.home.data.remote.dto.SupervisorStopSummaryDto
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.RouteStateEnum
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.SupervisorStopSummary

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

fun SupervisorRouteSummaryDto.toDomain() = SupervisorRouteSummary(
    name = name,
    user = user,
    date = date,
    origin = origin,
    destination = destination,
    state = state,
    stops = stops.map { it.toDomain() }
)

fun SupervisorStopSummaryDto.toDomain() = SupervisorStopSummary(
    state = state,
    direction = direction,
    order = order
)
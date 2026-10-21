package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.Route
import com.routeplanner.app.SelectRouteSummaries
import com.routeplanner.app.SelectRouteWithState
import com.routeplanner.app.core.paging.PagedResult
import com.routeplanner.app.features.home.data.remote.dto.PageResult
import com.routeplanner.app.features.home.data.remote.dto.SupervisorRouteSummaryDto
import com.routeplanner.app.features.home.data.remote.mapper.toDomain
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.model.UserRoute

fun Route.routeEntityMapper(
    state: String,
    userStops: List<UserStop>
) = UserRoute(
    id = id,
    state = state,
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
    userStops = userStops
)

fun SelectRouteWithState.toUserRoute(
    userStops: List<UserStop>
): UserRoute =
    UserRoute(
        id = id,
        state = stateDescription,
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
        encodedPolyline = encodedPolyline,
        userStops = userStops
    )

fun SelectRouteSummaries.toUserRouteSummary(): NotifierRouteSummary =
    NotifierRouteSummary(
        id = id,
        name = name,
        createdAt = createdAt
    )

fun PageResult<SupervisorRouteSummaryDto>.toDomain(): PagedResult<SupervisorRouteSummary> =
    PagedResult(
        items = items.map { it.toDomain() },
        page = page,
        pageSize = pageSize,
        totalItems = totalItems,
        totalPages = totalPages
    )


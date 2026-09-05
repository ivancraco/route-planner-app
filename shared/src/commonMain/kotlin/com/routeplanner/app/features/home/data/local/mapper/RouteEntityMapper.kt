package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.Route
import com.routeplanner.app.SelectRouteSummaries
import com.routeplanner.app.SelectRouteWithState
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
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
        userStops = userStops
    )

fun SelectRouteSummaries.toUserRouteSummary(): NotifierRouteSummary =
    NotifierRouteSummary(
        id = id,
        name = name,
        createdAt = createdAt
    )


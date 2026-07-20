package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.Route
import com.routeplanner.app.SelectRouteSummaries
import com.routeplanner.app.SelectRouteWithState
import com.routeplanner.app.features.home.domain.model.NotifierRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.NotifierStop

fun Route.routeEntityMapper(
    state: String,
    notifierStops: List<NotifierStop>
) = NotifierRoute(
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
    notifierStops = notifierStops
)

fun SelectRouteWithState.toNotifierRoute(
    notifierStops: List<NotifierStop>
): NotifierRoute =
    NotifierRoute(
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
        notifierStops = notifierStops
    )

fun SelectRouteSummaries.toNotifierRouteSummary(): NotifierRouteSummary =
    NotifierRouteSummary(
        id = id,
        name = name,
        createdAt = createdAt
    )


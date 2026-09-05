package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.SelectStopsWithDetailsByRouteId
import com.routeplanner.app.features.home.domain.model.UserStop

fun com.routeplanner.app.Stop.stopEntityMapper(
    state: String, notice: String
): UserStop =
    UserStop(
        id = id,
        routeId = routeId,
        notice = notice,
        state = state,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = orderNum.toInt(),
        note = note
    )

fun SelectStopsWithDetailsByRouteId.toUserStop(): UserStop =
    UserStop(
        id = id,
        routeId = routeId,
        notice = noticeDescription,
        state = stateDescription,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = orderNum.toInt(),
        note = note
    )
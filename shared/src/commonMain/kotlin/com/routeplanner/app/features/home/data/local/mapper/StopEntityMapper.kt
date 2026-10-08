package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.SelectById
import com.routeplanner.app.SelectPendingDelete
import com.routeplanner.app.SelectPendingSync
import com.routeplanner.app.SelectStopsWithDetailsByRouteId
import com.routeplanner.app.features.home.domain.model.UserStop

fun SelectById.toUserStop(
    notice: String
): UserStop =
    UserStop(
        id = id,
        routeId = routeId,
        notice = notice,
        state = stateDescription,
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

fun SelectPendingSync.toUserStop(
    noticeDescription: String
): UserStop =
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

fun SelectPendingDelete.toUserStop(notice: String): UserStop =
    UserStop(
        id = id,
        routeId = routeId,
        notice = notice,
        state = stateDescription,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = orderNum.toInt(),
        note = note
    )
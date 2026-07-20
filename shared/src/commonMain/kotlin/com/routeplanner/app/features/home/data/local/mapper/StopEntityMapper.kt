package com.routeplanner.app.features.home.data.local.mapper

import com.routeplanner.app.SelectStopsWithDetailsByRouteId
import com.routeplanner.app.features.home.domain.model.NotifierStop

fun com.routeplanner.app.Stop.stopEntityMapper(
    state: String, notice: String
): NotifierStop =
    NotifierStop(
        id = id,
        notice = notice,
        state = state,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = orderNum,
        note = note
    )

fun SelectStopsWithDetailsByRouteId.toNotifierStop(): NotifierStop =
    NotifierStop(
        id = id,
        notice = noticeDescription,
        state = stateDescription,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = orderNum,
        note = note
    )
package com.routeplanner.app.features.home.data.remote.mapper

import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum

fun CreateStopDto.toDomain(routeId: String): UserStop {
    return UserStop(
        id = id,
        routeId = routeId,
        notice = StopNoticeEnum.fromId(noticeId).description,
        state = StopNoticeEnum.fromId(stateId).description,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = order,
        note = note
    )
}
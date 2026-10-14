package com.routeplanner.app.features.home.data.remote.mapper

import com.routeplanner.app.Notice
import com.routeplanner.app.features.home.data.remote.dto.CreateStopDto
import com.routeplanner.app.features.home.data.remote.dto.NoticeDto
import com.routeplanner.app.features.home.data.remote.dto.StopStateDto
import com.routeplanner.app.features.home.domain.model.Notices
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum
import com.routeplanner.app.features.home.domain.model.StopState

fun CreateStopDto.toDomain(routeId: String): UserStop {
    return UserStop(
        id = id,
        routeId = routeId,
        noticeId = noticeId,
        stateId = stateId,
        recipient = recipient,
        direction = direction,
        directionPlaceId = directionPlaceId,
        latitude = latitude,
        longitude = longitude,
        order = order,
        note = note
    )
}

fun StopStateDto.toDomain() = StopState(id = id, description = description)
fun NoticeDto.toDomain() = Notices(id = id, description = description)

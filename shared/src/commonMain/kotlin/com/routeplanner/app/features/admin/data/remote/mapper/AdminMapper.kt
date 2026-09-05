package com.routeplanner.app.features.admin.data.remote.mapper

import com.routeplanner.app.features.admin.data.remote.dto.AdminRouteResponse
import com.routeplanner.app.features.admin.data.remote.dto.AdminStopResponse
import com.routeplanner.app.features.admin.domain.model.AdminRoute
import com.routeplanner.app.features.admin.domain.model.AdminStop

fun AdminRouteResponse.toAdminRoute() = AdminRoute(
    id = id,
    ownerName = ownerName,
    name = name,
    state = state,
    createdAt = createdAt,
    originDir = originDir,
    destinationDir = destinationDir,
    stops = stops.map { it.toAdminStop() }
)

fun AdminStopResponse.toAdminStop() = AdminStop(
    id = id,
    notice = notice,
    state = state,
    recipient = recipient,
    direction = direction,
    order = order ?: 0,
    note = note ?: "",
)
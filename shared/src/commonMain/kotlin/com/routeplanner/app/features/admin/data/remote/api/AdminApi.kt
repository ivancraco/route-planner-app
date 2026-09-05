package com.routeplanner.app.features.admin.data.remote.api

import com.routeplanner.app.features.admin.data.remote.dto.AdminRouteResponse

interface AdminApi {
    suspend fun getAllRoutes(): List<AdminRouteResponse>
}
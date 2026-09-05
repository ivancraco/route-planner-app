package com.routeplanner.app.features.admin.data.remote.api

import com.routeplanner.app.features.admin.data.remote.dto.AdminRouteResponse
import io.ktor.client.HttpClient

class AdminApiImpl(
    private val httpClient: HttpClient
): AdminApi {
    override suspend fun getAllRoutes(): List<AdminRouteResponse> {
        TODO("Not yet implemented")
    }
}
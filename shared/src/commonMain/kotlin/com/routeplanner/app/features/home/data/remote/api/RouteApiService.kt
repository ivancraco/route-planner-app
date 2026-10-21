package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.data.remote.dto.PageResult
import com.routeplanner.app.features.home.data.remote.dto.SupervisorRouteSummaryDto
import com.routeplanner.app.features.home.data.remote.dto.UpdateRouteDto
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop

interface RouteApiService {
    suspend fun upsertFromApi(userRoute: UserRoute, userId: Long)
    suspend fun createRoute(createRoute: CreateRouteDto): Boolean
    suspend fun updateRoute(route: UpdateRouteDto, id: String): Boolean
    suspend fun deleteFromApi(id: String): Boolean
    suspend fun pullFromApi(userId: Long): List<CreateRouteDto>
    suspend fun getPage(username: String?, page: Int, pageSize: Int): PageResult<SupervisorRouteSummaryDto>
}
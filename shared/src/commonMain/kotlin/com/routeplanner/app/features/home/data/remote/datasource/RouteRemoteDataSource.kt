package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.core.paging.PagedResult
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.UserRoute

interface RouteRemoteDataSource {
    suspend fun upsertFromApi(userRoute: UserRoute, userId: Long)
    suspend fun createRoute(userRoute: UserRoute)
    suspend fun updateRoute(userRoute: UserRoute)
    suspend fun deleteFromApi(id: String)
    suspend fun pullFromApi(userId: Long): List<UserRoute>
    suspend fun getPage(username: String?, page: Int, pageSize: Int): PagedResult<SupervisorRouteSummary>
}
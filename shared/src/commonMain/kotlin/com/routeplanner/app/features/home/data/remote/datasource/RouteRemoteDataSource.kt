package com.routeplanner.app.features.home.data.remote.datasource

import com.routeplanner.app.features.home.domain.model.UserRoute

interface RouteRemoteDataSource {
    suspend fun upsertFromApi(userRoute: UserRoute, userId: Long)
    suspend fun createRoute(userRoute: UserRoute)
    suspend fun updateRoute(userRoute: UserRoute)
    suspend fun deleteFromApi(id: String)
    suspend fun pullFromApi(userId: Long): List<UserRoute>
}
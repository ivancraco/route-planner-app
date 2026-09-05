package com.routeplanner.app.features.home.data.local.datasource

import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.UpdateRoute
import kotlinx.coroutines.flow.Flow

interface RouteLocalDataSource {
    fun observeRoute(id: String): Flow<UserRoute?>
    suspend fun insertRoute(userRoute: UserRoute, userId: Int): String
    suspend fun updateRoute(userRoute: UserRoute)
    suspend fun updateName(id: String, name: String)
    suspend fun updateState(id: String, stateId: Int)
    suspend fun updatePolyline(routeId: String, encodedPolyline: String)
    suspend fun softDelete(id: String)
    suspend fun selectAll(): List<UserRoute>
    suspend fun selectByUserId(userId: Long): List<UserRoute>
    suspend fun selectPendingSync(): List<UserRoute>
    suspend fun selectPendingDelete(): List<UserRoute>
    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>>
    suspend fun markAsSynced(id: String)
    suspend fun upsertFromApi(route: UpdateRoute, userId: Long)
    suspend fun selectById(id: String): UserRoute?
    suspend fun insertFromApi(userRoute: UserRoute, userId: Long): String
    suspend fun updateFromApi(userRoute: UserRoute)
    suspend fun deleteById(id: String)
    suspend fun enqueueSyncOperation(entityId: String, operation: String)
}
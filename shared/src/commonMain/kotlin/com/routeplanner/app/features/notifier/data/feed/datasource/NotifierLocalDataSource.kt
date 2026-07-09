package com.routeplanner.app.features.notifier.data.feed.datasource

import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import kotlinx.coroutines.flow.Flow

interface NotifierLocalDataSource {
    fun observeRoute(id: Long): Flow<NotifierRoute?>
    suspend fun insertRoute(routeEntity: NotifierRouteEntity): Long
    suspend fun updateRoute(notifierRoute: NotifierRoute)
    suspend fun updateName(id: Long, name: String)
    suspend fun updateState(id: Long, stateId: Long)
    suspend fun softDelete(id: Long)
    suspend fun selectAll(): List<NotifierRoute>
    suspend fun selectByUserId(userId: Long): List<NotifierRoute>
    suspend fun selectPendingSync(): List<NotifierRoute>
    suspend fun selectPendingDelete(): List<NotifierRoute>
    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>>
    suspend fun markAsSynced(id: Long)
    suspend fun upsertFromApi(notifierRouteSync: NotifierRouteSync, userId: Long)
}
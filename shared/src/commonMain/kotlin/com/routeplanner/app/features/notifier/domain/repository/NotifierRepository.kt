package com.routeplanner.app.features.notifier.domain.repository

import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import kotlinx.coroutines.flow.Flow

interface NotifierRepository {
    fun observeRoute(id: Long): Flow<NotifierRoute?>
    suspend fun insertRoute(routeEntity: NotifierRouteEntity): Long
    suspend fun updateRoute(notifierRouteItem: NotifierRoute)
    suspend fun updateName(id: Long, name: String)
    suspend fun updateState(id: Long, stateId: Long)
    suspend fun softDelete(id: Long)
    suspend fun selectAll(): Result<List<NotifierRoute>>
    suspend fun selectByUserId(userId: Long): Result<List<NotifierRoute>>
    suspend fun selectPendingSync(): Result<List<NotifierRoute>>
    suspend fun selectPendingDelete(): Result<List<NotifierRoute>>
    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>>
    suspend fun markAsSynced(id: Long)
    suspend fun upsertFromApi(notifierRouteSync: NotifierRouteSync, userId: Long)
}
package com.routeplanner.app.features.home.domain.repository

import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import kotlinx.coroutines.flow.Flow

interface NotifierRepository {
    fun observeRoute(id: Long): Flow<NotifierRoute?>
    suspend fun insertRoute(notifierRoute: NotifierRoute, userId: Long): Long
    suspend fun updateRoute(notifierRoute: NotifierRoute)
    suspend fun updateName(id: Long, name: String)
    suspend fun updateState(id: Long, stateId: Long)
    suspend fun softDelete(id: Long)
    suspend fun selectAll(): Result<List<NotifierRoute>>
    suspend fun selectById(id: Long): NotifierRoute?
    suspend fun selectByUserId(userId: Long): Result<List<NotifierRoute>>
    suspend fun selectPendingSync(): Result<List<NotifierRoute>>
    suspend fun selectPendingDelete(): Result<List<NotifierRoute>>
    // Solo para uso interno
    suspend fun getLocalById(id: Long): NotifierRoute? = selectById(id)
    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>>
    suspend fun markAsSynced(id: Long)
    suspend fun upsertFromApi(notifierRouteSync: NotifierRouteSync, userId: Long)
    suspend fun pushToApi(notifierRoute: NotifierRoute)
    suspend fun deleteFromApi(id: Long)
    suspend fun pullFromApi(userId: Long)
}
package com.routeplanner.app.features.home.domain.repository

import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.flow.Flow

interface StopRepository {
    fun observeStopsByRouteId(routeId: String): Flow<List<UserStop>>
    suspend fun insertStop(stop: UserStop, routeId: String): String
    suspend fun updateStop(stop: UserStop)
    suspend fun updateState(id: String, stateId: Long)
    suspend fun softDelete(id: String)
    suspend fun softDeleteByRouteId(routeId: String)
    suspend fun selectById(id: String): UserStop?
    suspend fun selectByIdIncludingDeleted(id: String): UserStop
    suspend fun pullFromApi(routeId: String)
    // Sync interno — usado por SyncManager
    suspend fun pushToApi(stop: UserStop, routeId: String, operation: String)
    suspend fun deleteFromApi(id: String, routeId: String)
    suspend fun markAsSynced(id: String)
    suspend fun deletePermanently(id: String)
}
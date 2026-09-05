package com.routeplanner.app.features.home.data.local.datasource

import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.flow.Flow

interface StopLocalDataSource {
    fun observeStopsByRouteId(routeId: String): Flow<List<UserStop>>
    suspend fun insertStop(stop: UserStop, routeId: String): String
    suspend fun updateStop(stop: UserStop)
    suspend fun updateState(id: String, stateId: Long)
    suspend fun softDelete(id: String)
    suspend fun softDeleteByRouteId(routeId: String)
    suspend fun selectById(id: String): UserStop?
    suspend fun selectByRouteId(routeId: String): List<UserStop>
    suspend fun selectPendingSync(): List<UserStop>
    suspend fun selectPendingDelete(): List<UserStop>
    suspend fun selectByIdIncludingDeleted(id: String): UserStop
    suspend fun markAsSynced(id: String)
    suspend fun deleteById(id: String)
    suspend fun enqueueSyncOperation(entityId: String, operation: String)
    suspend fun upsertFromApi(stop: UserStop, routeId: String)
}
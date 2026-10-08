package com.routeplanner.app.features.home.domain.repository

import com.routeplanner.app.features.home.data.remote.dto.StopOrderDto
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.flow.Flow

interface StopRepository {
    fun observeStopsByRouteId(routeId: String): Flow<List<UserStop>>
    suspend fun selectByRouteId(routeId: String): List<UserStop>
    suspend fun insertStop(stop: UserStop, routeId: String): String
    suspend fun updateStop(stop: UserStop)
    suspend fun updateState(id: String, stateId: Long)
    suspend fun reorderStopsLocally(routeId: String, reorderedStops: List<UserStop>)
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
    suspend fun reorderInApi(routeId: String, stops: List<StopOrderDto>)
    fun observeAllStates(): Flow<List<StopState>>
    suspend fun getAllStates(): List<StopState>
    suspend fun syncStateFromApi(): Result<Unit>
}
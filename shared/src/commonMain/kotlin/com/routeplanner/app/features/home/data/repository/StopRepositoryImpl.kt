package com.routeplanner.app.features.home.data.repository

import com.routeplanner.app.core.utils.SyncManager
import com.routeplanner.app.core.utils.SyncOperation
import com.routeplanner.app.features.home.data.local.datasource.StopLocalDataSource
import com.routeplanner.app.features.home.data.remote.datasource.StopRemoteDataSource
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.repository.StopRepository

class StopRepositoryImpl(
    private val localDataSource: StopLocalDataSource,
    private val remoteDataSource: StopRemoteDataSource,
    private val syncManager: Lazy<SyncManager>
) : StopRepository {

    override fun observeStopsByRouteId(routeId: String) =
        localDataSource.observeStopsByRouteId(routeId)

    override suspend fun insertStop(stop: UserStop, routeId: String): String {
        println("--onCreateStop--2")
        localDataSource.insertStop(stop, routeId)
        localDataSource.enqueueSyncOperation(stop.id, SyncOperation.INSERT)
        syncManager.value.syncNowAsync()
        return stop.id
    }

    override suspend fun updateStop(stop: UserStop) {
        localDataSource.updateStop(stop)
        localDataSource.enqueueSyncOperation(stop.id, SyncOperation.UPDATE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun updateState(id: String, stateId: Long) {
        localDataSource.updateState(id, stateId)
        localDataSource.enqueueSyncOperation(id, SyncOperation.UPDATE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun softDelete(id: String) {
        localDataSource.softDelete(id)
        localDataSource.enqueueSyncOperation(id, SyncOperation.DELETE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun softDeleteByRouteId(routeId: String) {
        val stops = localDataSource.selectByRouteId(routeId)
        localDataSource.softDeleteByRouteId(routeId)
        stops.forEach {
            localDataSource.enqueueSyncOperation(it.id, SyncOperation.DELETE)
        }
        syncManager.value.syncNowAsync()
    }

    override suspend fun selectById(id: String) =
        localDataSource.selectById(id)

    override suspend fun selectByIdIncludingDeleted(id: String): UserStop =
        localDataSource.selectByIdIncludingDeleted(id)

    override suspend fun pullFromApi(routeId: String) {
        val stops = remoteDataSource.pullFromApi(routeId)
        stops.forEach { localDataSource.upsertFromApi(it, routeId) }
    }

    override suspend fun pushToApi(stop: UserStop, routeId: String, operation: String) =
        remoteDataSource.pushToApi(stop, routeId, operation)

    override suspend fun deleteFromApi(id: String, routeId: String) =
        remoteDataSource.deleteFromApi(id, routeId)

    override suspend fun markAsSynced(id: String) =
        localDataSource.markAsSynced(id)

    override suspend fun deletePermanently(id: String) =
        localDataSource.deleteById(id)
}
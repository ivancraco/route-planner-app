package com.routeplanner.app.features.home.data.local.datasource

import com.routeplanner.app.features.home.data.local.dao.StopDao
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.flow.Flow

class StopLocalDataSourceImpl(
    private val stopDao: StopDao
) : StopLocalDataSource {
    override fun observeStopsByRouteId(routeId: String) =
        stopDao.observeStopsByRouteId(routeId)

    override suspend fun insertStop(stop: UserStop, routeId: String) =
        stopDao.insertStop(stop, routeId)

    override suspend fun updateStop(stop: UserStop) =
        stopDao.updateStop(stop)

    override suspend fun updateState(id: String, stateId: Long) =
        stopDao.updateState(id, stateId)

    override suspend fun softDelete(id: String) =
        stopDao.softDelete(id)

    override suspend fun softDeleteByRouteId(routeId: String) =
        stopDao.softDeleteByRouteId(routeId)

    override suspend fun selectById(id: String) =
        stopDao.selectById(id)

    override suspend fun selectByRouteId(routeId: String) =
        stopDao.selectByRouteId(routeId)

    override suspend fun selectPendingSync() =
        stopDao.selectPendingSync()

    override suspend fun selectPendingDelete() =
        stopDao.selectPendingDelete()

    override suspend fun selectByIdIncludingDeleted(id: String): UserStop =
        stopDao.selectByIdIncludingDeleted(id)

    override suspend fun markAsSynced(id: String) =
        stopDao.markAsSynced(id)

    override suspend fun deleteById(id: String) =
        stopDao.deleteById(id)

    override suspend fun enqueueSyncOperation(entityId: String, operation: String, entity: String) =
        stopDao.enqueueSyncOperation(entityId, operation, entity)

    override suspend fun upsertFromApi(stop: UserStop, routeId: String) =
        stopDao.upsertFromApi(stop, routeId)

    override suspend fun updateStopOrderLocal(id: String, order: Int) {
        stopDao.updateStopOrderLocal(id, order)
    }

    override fun observeAllStates(): Flow<List<StopState>> {
        return stopDao.observeAllStates()
    }

    override suspend fun getAllStates(): List<StopState> {
        return stopDao.getAllStates()
    }

    override suspend fun upsertState(stopState: StopState) {
        stopDao.upsertState(stopState)
    }

    override suspend fun upsertAllStates(stopStates: List<StopState>) {
        stopDao.upsertAllStates(stopStates)
    }
}
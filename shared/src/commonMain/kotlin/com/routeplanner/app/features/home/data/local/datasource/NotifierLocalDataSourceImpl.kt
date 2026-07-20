package com.routeplanner.app.features.home.data.local.datasource

import com.routeplanner.app.features.home.data.local.dao.NotifierRouteDao
import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import kotlinx.coroutines.flow.Flow

class NotifierLocalDataSourceImpl(
    private val notifierRouteDao: NotifierRouteDao,
): NotifierLocalDataSource {
    override fun observeRoute(id: Long): Flow<NotifierRoute?> {
        return notifierRouteDao.observeRoute(id = id)
    }

    override suspend fun insertRoute(
        notifierRoute: NotifierRoute,
        userId: Long
    ): Long {
        return notifierRouteDao.insertRoute(
            notifierRoute = notifierRoute,
            userId = userId
        )
    }

    override suspend fun updateRoute(
        notifierRoute: NotifierRoute
    ) {
        notifierRouteDao.updateRoute(notifierRoute = notifierRoute)
    }

    override suspend fun updateName(id: Long, name: String) {
        notifierRouteDao.updateName(
            id = id,
            name = name
        )
    }

    override suspend fun updateState(id: Long, stateId: Long) {
        notifierRouteDao.updateState(
            id = id,
            stateId = stateId
        )
    }

    override suspend fun softDelete(id: Long) {
        notifierRouteDao.softDelete(id = id)
    }

    override suspend fun selectAll(): List<NotifierRoute> {
        return notifierRouteDao.selectAll()
    }

    override suspend fun selectByUserId(userId: Long): List<NotifierRoute> {
        return notifierRouteDao.selectByUserId(userId = userId)
    }

    override suspend fun selectPendingSync(): List<NotifierRoute> {
        return notifierRouteDao.selectPendingSync()
    }

    override suspend fun selectPendingDelete(): List<NotifierRoute> {
        return notifierRouteDao.selectPendingDelete()
    }

    override suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> {
        return notifierRouteDao.observeRouteSummaries()
    }

    override suspend fun markAsSynced(id: Long) {
        notifierRouteDao.markAsSynced(id = id)
    }

    override suspend fun upsertFromApi(
        notifierRouteSync: NotifierRouteSync,
        userId: Long
    ) {
        notifierRouteDao.upsertFromApi(
            notifierRouteSync = notifierRouteSync,
            userId = userId
        )
    }
}
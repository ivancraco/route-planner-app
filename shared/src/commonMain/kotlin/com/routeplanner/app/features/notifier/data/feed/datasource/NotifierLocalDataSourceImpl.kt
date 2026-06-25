package com.routeplanner.app.features.notifier.data.feed.datasource

import com.routeplanner.app.features.notifier.data.local.dao.NotifierRouteDao
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import kotlinx.coroutines.flow.Flow

class NotifierLocalDataSourceImpl(
    private val notifierRouteDao: NotifierRouteDao,
): NotifierLocalDataSource {
    override suspend fun insertRoute(
        routeEntity: NotifierRouteEntity
    ): NotifierRoute {
        return notifierRouteDao.insertRoute(
            routeEntity = routeEntity
        )
    }

    override suspend fun updateRoute(
        notifierRoute: NotifierRoute,
        stateId: Long
    ) {
        notifierRouteDao.updateRoute(
            notifierRouteItem = notifierRoute,
            stateId = stateId
        )
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

    override suspend fun selectById(id: Long): NotifierRoute {
        return notifierRouteDao.selectById(id = id)
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
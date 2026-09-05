package com.routeplanner.app.features.home.data.local.datasource

import com.routeplanner.app.features.home.data.local.dao.RouteDao
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.UpdateRoute
import kotlinx.coroutines.flow.Flow

class RouteLocalDataSourceImpl(
    private val routeDao: RouteDao,
): RouteLocalDataSource {
    override fun observeRoute(id: String): Flow<UserRoute?> {
        return routeDao.observeRoute(id = id)
    }

    override suspend fun insertRoute(
        userRoute: UserRoute,
        userId: Int
    ): String {
        return routeDao.insertRoute(
            userRoute = userRoute,
            userId = userId
        )
    }

    override suspend fun updateRoute(
        userRoute: UserRoute
    ) {
        routeDao.updateRoute(userRoute = userRoute)
    }

    override suspend fun updateName(id: String, name: String) {
        routeDao.updateName(
            id = id,
            name = name
        )
    }

    override suspend fun updateState(id: String, stateId: Int) {
        routeDao.updateState(
            id = id,
            stateId = stateId
        )
    }

    override suspend fun updatePolyline(routeId: String, encodedPolyline: String) {
        routeDao.updatePolyline(
            routeId = routeId,
            encodedPolyline = encodedPolyline
        )
    }

    override suspend fun softDelete(id: String) {
        routeDao.softDelete(id = id)
    }

    override suspend fun selectAll(): List<UserRoute> {
        return routeDao.selectAll()
    }

    override suspend fun selectByUserId(userId: Long): List<UserRoute> {
        return routeDao.selectByUserId(userId = userId)
    }

    override suspend fun selectPendingSync(): List<UserRoute> {
        return routeDao.selectPendingSync()
    }

    override suspend fun selectPendingDelete(): List<UserRoute> {
        return routeDao.selectPendingDelete()
    }

    override suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> {
        return routeDao.observeRouteSummaries()
    }

    override suspend fun markAsSynced(id: String) {
        routeDao.markAsSynced(id = id)
    }

    override suspend fun selectById(id: String): UserRoute {
        return routeDao.selectById(id)
    }

    override suspend fun insertFromApi(
        userRoute: UserRoute,
        userId: Long
    ): String {
        return routeDao.insertFromApi(userRoute, userId)
    }

    override suspend fun updateFromApi(userRoute: UserRoute) {
        routeDao.updateFromApi(userRoute)
    }

    override suspend fun deleteById(id: String) {
        routeDao.deleteById(id = id)
    }

    override suspend fun enqueueSyncOperation(entityId: String, operation: String) {
        routeDao.enqueueSyncOperation(entityId, operation)
    }

    override suspend fun upsertFromApi(route: UpdateRoute, userId: Long) {
        /*otifierRouteDao.upsertFromApi(
            // NotifierRouteSync lo construís desde NotifierRoute
            createRouteDto = notifierRoute.toDto(),
            userId = userId
        )*/
    }
}
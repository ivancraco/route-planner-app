package com.routeplanner.app.features.notifier.data.repository

import com.routeplanner.app.features.notifier.data.feed.datasource.NotifierLocalDataSource
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.notifier.domain.repository.NotifierRepository
import kotlinx.coroutines.flow.Flow

class NotifierRepositoryImpl(
    private val notifierLocalDataSource: NotifierLocalDataSource,
    //private val notifierRemoteDataSource: NotifierRemoteDataSource
): NotifierRepository {

    override fun observeRoute(id: Long): Flow<NotifierRoute?> {
        return notifierLocalDataSource.observeRoute(id = id)
    }

    override suspend fun insertRoute(
        routeEntity: NotifierRouteEntity
    ): Long {
        return notifierLocalDataSource.insertRoute(
            routeEntity = routeEntity
        )
    }

    override suspend fun updateRoute(
        notifierRouteItem: NotifierRoute
    ) {
        notifierLocalDataSource.updateRoute(
            notifierRoute = notifierRouteItem
        )
    }

    override suspend fun updateName(id: Long, name: String) {
        notifierLocalDataSource.updateName(
            id = id,
            name = name
        )
    }

    override suspend fun updateState(id: Long, stateId: Long) {
        notifierLocalDataSource.updateState(
            id = id,
            stateId = stateId
        )
    }

    override suspend fun softDelete(id: Long) {
        notifierLocalDataSource.softDelete(id = id)
    }

    override suspend fun selectAll(): Result<List<NotifierRoute>> {
        try {
            val routes = notifierLocalDataSource.selectAll()
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun selectByUserId(userId: Long): Result<List<NotifierRoute>> {
        try {
            val routes = notifierLocalDataSource.selectByUserId(userId = userId)
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun selectPendingSync(): Result<List<NotifierRoute>> {
        try {
            val routes = notifierLocalDataSource.selectPendingSync()
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun selectPendingDelete(): Result<List<NotifierRoute>> {
        try {
            val routes = notifierLocalDataSource.selectPendingDelete()
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> {
        return notifierLocalDataSource.observeRouteSummaries()
    }

    override suspend fun markAsSynced(id: Long) {
        notifierLocalDataSource.markAsSynced(id = id)
    }

    override suspend fun upsertFromApi(
        notifierRouteSync: NotifierRouteSync,
        userId: Long
    ) {
        // Llamar a la api y pasar la respuesta al parámetro de la función
        notifierLocalDataSource.upsertFromApi(
            notifierRouteSync = notifierRouteSync,
            userId = userId
        )
    }
}
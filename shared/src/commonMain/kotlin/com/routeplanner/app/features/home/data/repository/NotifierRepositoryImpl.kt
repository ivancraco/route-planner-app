package com.routeplanner.app.features.home.data.repository

import com.routeplanner.app.features.home.data.local.datasource.NotifierLocalDataSource
import com.routeplanner.app.features.home.data.remote.datasource.NotifierRemoteDataSource
import com.routeplanner.app.features.home.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.home.domain.model.NotifierRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.repository.NotifierRepository
import kotlinx.coroutines.flow.Flow

class NotifierRepositoryImpl(
    private val notifierLocalDataSource: NotifierLocalDataSource,
    private val notifierRemoteDataSource: NotifierRemoteDataSource
): NotifierRepository {

    override fun observeRoute(id: Long): Flow<NotifierRoute?> {
        return notifierLocalDataSource.observeRoute(id = id)
    }

    override suspend fun insertRoute(
        notifierRoute: NotifierRoute,
        userId: Long
    ): Long {
        return notifierLocalDataSource.insertRoute(
            notifierRoute = notifierRoute,
            userId = userId
        )
    }

    override suspend fun updateRoute(
        notifierRoute: NotifierRoute
    ) {
        notifierLocalDataSource.updateRoute(
            notifierRoute = notifierRoute
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

    override suspend fun selectById(id: Long): NotifierRoute? {
        TODO("Not yet implemented")
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

    override suspend fun pushToApi(notifierRoute: NotifierRoute) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFromApi(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun pullFromApi(userId: Long) {
        TODO("Not yet implemented")
    }
}
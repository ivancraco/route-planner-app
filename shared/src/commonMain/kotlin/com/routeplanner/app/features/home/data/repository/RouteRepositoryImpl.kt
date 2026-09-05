package com.routeplanner.app.features.home.data.repository

import com.routeplanner.app.core.utils.SyncManager
import com.routeplanner.app.core.utils.SyncOperation
import com.routeplanner.app.features.home.data.local.datasource.RouteLocalDataSource
import com.routeplanner.app.features.home.data.remote.api.OptimalRouteResult
import com.routeplanner.app.features.home.data.remote.api.ReverseGeocodingApi
import com.routeplanner.app.features.home.data.remote.api.RoutesApi
import com.routeplanner.app.features.home.data.remote.datasource.RouteRemoteDataSource
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.repository.RouteRepository
import kotlinx.coroutines.flow.Flow

class RouteRepositoryImpl(
    private val routeLocalDataSource: RouteLocalDataSource,
    private val routeRemoteDataSource: RouteRemoteDataSource,
    private val syncManager: Lazy<SyncManager>,
    private val reverseGeocodingApi: ReverseGeocodingApi,
    private val optimalRouteApi: RoutesApi
) : RouteRepository {

    override fun observeRoute(id: String): Flow<UserRoute?> {
        return routeLocalDataSource.observeRoute(id = id)
    }

    override suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> {
        return routeLocalDataSource.observeRouteSummaries()
    }

    override suspend fun selectAll(): List<UserRoute> {
        return routeLocalDataSource.selectAll()
    }

    override suspend fun selectByUserId(userId: Long): List<UserRoute> {
        return routeLocalDataSource.selectByUserId(userId = userId)
    }

    override suspend fun insertRoute(
        userRoute: UserRoute,
        userId: Int
    ): String {
        println("--onCreateRoute--2")
        val newId = routeLocalDataSource.insertRoute(
            userRoute = userRoute,
            userId = userId
        )
        routeLocalDataSource.enqueueSyncOperation(newId, SyncOperation.INSERT)
        println("-----1-----")
        syncManager.value.syncNowAsync()
        return newId
    }

    override suspend fun updateRoute(
        userRoute: UserRoute
    ) {
        routeLocalDataSource.updateRoute(
            userRoute = userRoute
        )
        routeLocalDataSource.enqueueSyncOperation(userRoute.id, SyncOperation.UPDATE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun updateName(id: String, name: String) {
        routeLocalDataSource.updateName(
            id = id,
            name = name
        )
        routeLocalDataSource.enqueueSyncOperation(id, SyncOperation.UPDATE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun updateState(id: String, stateId: Int) {
        routeLocalDataSource.updateState(
            id = id,
            stateId = stateId
        )
        routeLocalDataSource.enqueueSyncOperation(id, SyncOperation.UPDATE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun softDelete(id: String) {
        routeLocalDataSource.softDelete(id = id)
        routeLocalDataSource.enqueueSyncOperation(id, SyncOperation.DELETE)
        syncManager.value.syncNowAsync()
    }

    override suspend fun selectById(id: String): UserRoute? {
        return routeLocalDataSource.selectById(id)
    }


    override suspend fun selectPendingSync(): Result<List<UserRoute>> {
        try {
            val routes = routeLocalDataSource.selectPendingSync()
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun selectPendingDelete(): Result<List<UserRoute>> {
        try {
            val routes = routeLocalDataSource.selectPendingDelete()
            return Result.success(routes)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun markAsSynced(id: String) {
        routeLocalDataSource.markAsSynced(id = id)
    }

    override suspend fun upsertFromApi(
        userRoute: UserRoute,
        userId: Long
    ) {
        routeRemoteDataSource.upsertFromApi(
            userRoute = userRoute,
            userId = userId
        )
    }

    override suspend fun insertRouteToApi(userRoute: UserRoute) {
        routeRemoteDataSource.createRoute(userRoute)
    }

    override suspend fun updateRouteToApi(userRoute: UserRoute) {
        routeRemoteDataSource.updateRoute(userRoute)
    }

    override suspend fun deleteRouteToApi(id: String) {
        routeRemoteDataSource.deleteFromApi(id = id)
    }

    override suspend fun pullFromApi(userId: Long) {
        val routes = routeRemoteDataSource.pullFromApi(userId)
        routes.forEach { route ->
            // upsertFromApi ya maneja la lógica de is_synced internamente en el DAO
            //notifierLocalDataSource.upsertFromApi(route, userId)
        }
    }

    override suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<String> = reverseGeocodingApi.getAddress(latitude, longitude)

    override suspend fun computeOptimalRoute(
        originLatitude: Double,
        originLongitude: Double,
        stops: List<UserStop>
    ): Result<OptimalRouteResult> = optimalRouteApi.computeOptimalRoute(
        originLatitude = originLatitude,
        originLongitude = originLongitude,
        stops = stops
    )

    override suspend fun updatePolyline(routeId: String, encodedPolyline: String) {
        routeLocalDataSource.updatePolyline(routeId, encodedPolyline)
    }

    override suspend fun deletePermanently(id: String) {
        routeLocalDataSource.deleteById(id)
    }
}
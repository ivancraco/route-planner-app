package com.routeplanner.app.features.home.domain.repository

import com.routeplanner.app.features.home.data.remote.api.OptimalRouteResult
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    //Lectura
    fun observeRoute(id: String): Flow<UserRoute?>
    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>>
    suspend fun selectAll(): List<UserRoute>
    suspend fun selectById(id: String): UserRoute?
    suspend fun selectByUserId(userId: Long): List<UserRoute>

    //Escritura local + sync
    suspend fun insertRoute(userRoute: UserRoute, userId: Int): String
    suspend fun updateRoute(userRoute: UserRoute)
    suspend fun updateName(id: String, name: String)
    suspend fun updateState(id: String, stateId: Int)
    suspend fun softDelete(id: String)

    // Sync interno (usado por SyncManager)
    suspend fun insertRouteToApi(userRoute: UserRoute)
    suspend fun updateRouteToApi(userRoute: UserRoute)
    suspend fun deleteRouteToApi(id: String)
    suspend fun markAsSynced(id: String)
    suspend fun deletePermanently(id: String)

    //Pull (primer login)
    suspend fun pullFromApi(userId: Long)

    suspend fun getAddressFromCoordinates(
        latitude: Double,
        longitude: Double
    ): Result<String>

    suspend fun computeOptimalRoute(
        originLatitude: Double,
        originLongitude: Double,
        stops: List<UserStop>
    ): Result<OptimalRouteResult>

    suspend fun updatePolyline(routeId: String, encodedPolyline: String)


    //--------------------------------------
    suspend fun selectPendingSync(): Result<List<UserRoute>>
    suspend fun selectPendingDelete(): Result<List<UserRoute>>

    // Solo para uso interno
    suspend fun getLocalById(id: String): UserRoute? = selectById(id)
    suspend fun upsertFromApi(userRoute: UserRoute, userId: Long)
}
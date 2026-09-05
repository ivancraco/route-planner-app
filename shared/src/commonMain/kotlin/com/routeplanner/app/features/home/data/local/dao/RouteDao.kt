package com.routeplanner.app.features.home.data.local.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.routeplanner.app.core.common.data.database.DbHelper
import com.routeplanner.app.core.utils.SyncEntity
import com.routeplanner.app.features.home.data.local.mapper.toUserRoute
import com.routeplanner.app.features.home.data.local.mapper.toUserRouteSummary
import com.routeplanner.app.features.home.data.local.mapper.toUserStop
import com.routeplanner.app.features.home.data.remote.dto.CreateRouteDto
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.RouteStateEnum
import com.routeplanner.app.features.home.domain.model.UserRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class RouteDao(
    private val dbHelper: DbHelper
) {
    fun observeRoute(
        id: String
    ): Flow<UserRoute?> {
        return dbHelper.withDatabaseFlow { database ->
            println("-----id: $id")
            /*val stops = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .awaitAsList()
                .map { it.toNotifierStop() }*/

            val stopsFlow = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { list -> list.map { it.toUserStop() } }

            val routeFlow = database.routeQueries
                .selectRouteWithState(id)
                .asFlow()
                .mapToOneOrNull(Dispatchers.Default)

            combine(routeFlow, stopsFlow) { route, stops ->
                route?.toUserRoute(stops)
            }
        }
    }

    suspend fun insertRoute(
        userRoute: UserRoute,
        userId: Int
    ): String {
        return dbHelper.withDatabase { database ->
            println("--onCreateRoute--3")
            //val newId = generateId()
            database.routeQueries.insert(
                id = userRoute.id,
                userId = userId.toLong(),
                stateId = RouteStateEnum.fromName(userRoute.state).id.toLong(),
                name = userRoute.name,
                createdAt = userRoute.createdAt,
                originDir = userRoute.originDir,
                originPlaceId = userRoute.originPlaceId,
                originLatitude = userRoute.originLatitude,
                originLongitude = userRoute.originLongitude,
                destinationDir = userRoute.destinationDir,
                destinationPlaceId = userRoute.destinationPlaceId,
                destinationLatitude = userRoute.destinationLatitude,
                destinationLongitude = userRoute.destinationLongitude,
                encodedPolyline = null
            )
            userRoute.id
            //database.routeQueries.lastInsertRowId().executeAsOne()
            /*val id = database.routeQueries.lastInsertRowId().executeAsOne()
            val stops = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .awaitAsList()
                .map { it.toNotifierStop() }
            database.routeQueries.selectRouteWithState(id).executeAsOne()
                .toNotifierRoute(stops)*/
        }
    }

    suspend fun updateRoute(
        userRoute: UserRoute
    ) {
        dbHelper.withDatabase { database ->
            /*database.routeQueries.update(
                id = route.id,
                stateId = RouteStateEnum.fromName(route.state).id,
                name = route.name,
                originDir = route.originDir,
                originPlaceId = route.originPlaceId,
                originLatitude = route.originLatitude,
                originLongitude = route.originLongitude,
                destinationDir = route.destinationDir,
                destinationPlaceId = route.destinationPlaceId,
                destinationLatitude = route.destinationLatitude,
                destinationLongitude = route.destinationLongitude
            )*/
        }
    }

    suspend fun updateName(
        id: String,
        name: String
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.updateName(
                id = id,
                name = name
            )
        }
    }

    suspend fun updateState(
        id: String,
        stateId: Int
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.updateState(
                id = id,
                stateId = stateId.toLong()
            )
        }
    }

    suspend fun updatePolyline(routeId: String, encodedPolyline: String) {
        dbHelper.withDatabase { database ->
            database.routeQueries.updatePolyline(
                encodedPolyline = encodedPolyline,
                id = routeId
            )
        }
    }

    suspend fun softDelete(
        id: String
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.softDelete(
                id = id
            )
        }
    }

    suspend fun selectAll(): List<UserRoute> {
        return dbHelper.withDatabase { database ->
            listOf()
        }
    }

    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> =
        dbHelper.withDatabase { db ->
            db.routeQueries.selectRouteSummaries()
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { it.map { route -> route.toUserRouteSummary() } }
        }

    suspend fun selectByUserId(
        userId: Long
    ): List<UserRoute> {
        return dbHelper.withDatabase { database ->
            listOf()
            /*val state = database.routeStateQueries.selectAll().awaitAsList()
            val notice = database.noticeQueries.selectAll().awaitAsList()
            val stops = database.stopQueries.selectAll().awaitAsList()
                .map {
                    it.stopEntityMapper(
                        state = state.first { s -> s.id == it.id }.description,
                        notice = notice.first { n -> n.id == it.id }.description
                    )
                }
            database.routeQueries.selectByUserId(userId = userId).awaitAsList()
                .map { routeEntity ->
                    routeEntity.routeEntityMapper(
                        state = state.first { s -> s.id == routeEntity.id }.description,
                        notifierStops = stops.filter { it.id == routeEntity.id }
                    )
                }*/
        }
    }

    suspend fun selectPendingSync(): List<UserRoute> {
        return dbHelper.withDatabase { database ->
            listOf()
            /*val state = database.routeStateQueries.selectAll().awaitAsList()
            val notice = database.noticeQueries.selectAll().awaitAsList()
            val stops = database.stopQueries.selectAll().awaitAsList()
                .map {
                    it.stopEntityMapper(
                        state = state.first { s -> s.id == it.id }.description,
                        notice = notice.first { n -> n.id == it.id }.description
                    )
                }
            database.routeQueries.selectPendingSync().awaitAsList().map { routeEntity ->
                routeEntity.routeEntityMapper(
                    state = state.first { s -> s.id == routeEntity.id }.description,
                    notifierStops = stops.filter { it.id == routeEntity.id }
                )
            }*/
        }
    }

    suspend fun selectPendingDelete(): List<UserRoute> {
        return dbHelper.withDatabase { database ->
            listOf()
            /*val state = database.routeStateQueries.selectAll().awaitAsList()
            val notice = database.noticeQueries.selectAll().awaitAsList()
            val stops = database.stopQueries.selectAll().awaitAsList()
                .map {
                    it.stopEntityMapper(
                        state = state.first { s -> s.id == it.id }.description,
                        notice = notice.first { n -> n.id == it.id }.description
                    )
                }
            database.routeQueries.selectPendingDelete().awaitAsList().map { routeEntity ->
                routeEntity.routeEntityMapper(
                    state = state.first { s -> s.id == routeEntity.id }.description,
                    notifierStops = stops.filter { it.id == routeEntity.id }
                )
            }*/
        }
    }

    suspend fun markAsSynced(id: String) {
        dbHelper.withDatabase { database ->
            database.routeQueries.markAsSynced(id = id)
        }
    }

    suspend fun upsertFromApi(createRouteDto: CreateRouteDto, userId: Long) {
        dbHelper.withDatabase { database ->
            val existing =
                database.routeQueries.selectById(createRouteDto.id).executeAsOneOrNull()
            when {
                existing == null -> database.routeQueries.insertFromApi(
                    id = createRouteDto.id,
                    userId = userId,
                    stateId = createRouteDto.stateId.toLong(),
                    name = createRouteDto.name,
                    createdAt = createRouteDto.createdAt,
                    originDir = createRouteDto.originDir,
                    originPlaceId = createRouteDto.originPlaceId,
                    originLatitude = createRouteDto.originLatitude,
                    originLongitude = createRouteDto.originLongitude,
                    destinationDir = createRouteDto.destinationDir,
                    destinationPlaceId = createRouteDto.destinationPlaceId,
                    destinationLatitude = createRouteDto.destinationLatitude,
                    destinationLongitude = createRouteDto.destinationLongitude
                )   // registro nuevo
                existing.isSynced == 1L -> database.routeQueries.updateFromApi(
                    id = createRouteDto.id,
                    stateId = createRouteDto.stateId.toLong(),
                    name = createRouteDto.name,
                    createdAt = createRouteDto.createdAt,
                    originDir = createRouteDto.originDir,
                    originPlaceId = createRouteDto.originPlaceId,
                    originLatitude = createRouteDto.originLatitude,
                    originLongitude = createRouteDto.originLongitude,
                    destinationDir = createRouteDto.destinationDir,
                    destinationPlaceId = createRouteDto.destinationPlaceId,
                    destinationLatitude = createRouteDto.destinationLatitude,
                    destinationLongitude = createRouteDto.destinationLongitude
                )  // pisa solo si está sincronizado
                // is_synced == 0 → tiene cambios locales pendientes, no se pisa
            }
        }
    }

    suspend fun selectById(id: String): UserRoute {
        return dbHelper.withDatabase { database ->

            val stops = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .awaitAsList()
                .map { it.toUserStop() }

            val route = database.routeQueries
                .selectRouteWithState(id)
                .executeAsOne()

            route.toUserRoute(stops)
        }
    }

    suspend fun insertFromApi(userRoute: UserRoute, userId: Long): String {
        return dbHelper.withDatabase { database ->
            database.routeQueries.insertFromApi(
                id = userRoute.id,
                userId = userId,
                stateId = RouteStateEnum.fromName(userRoute.state).id.toLong(),
                name = userRoute.name,
                createdAt = userRoute.createdAt,
                originDir = userRoute.originDir,
                originPlaceId = userRoute.originPlaceId,
                originLatitude = userRoute.originLatitude,
                originLongitude = userRoute.originLongitude,
                destinationDir = userRoute.destinationDir,
                destinationPlaceId = userRoute.destinationPlaceId,
                destinationLatitude = userRoute.destinationLatitude,
                destinationLongitude = userRoute.destinationLongitude
            )
            userRoute.id
            //database.routeQueries.lastInsertRowId().executeAsOne()
        }
    }

    suspend fun updateFromApi(userRoute: UserRoute) {
        dbHelper.withDatabase { database ->
            database.routeQueries.updateFromApi(
                id = userRoute.id,
                stateId = RouteStateEnum.fromName(userRoute.state).id.toLong(),
                name = userRoute.name,
                createdAt = userRoute.createdAt,
                originDir = userRoute.originDir,
                originPlaceId = userRoute.originPlaceId,
                originLatitude = userRoute.originLatitude,
                originLongitude = userRoute.originLongitude,
                destinationDir = userRoute.destinationDir,
                destinationPlaceId = userRoute.destinationPlaceId,
                destinationLatitude = userRoute.destinationLatitude,
                destinationLongitude = userRoute.destinationLongitude
            )
        }
    }

    suspend fun deleteById(id: String) {
        dbHelper.withDatabase { database ->
            database.routeQueries.deleteById(id = id)
        }
    }

    suspend fun enqueueSyncOperation(entityId: String, operation: String) {
        dbHelper.withDatabase { database ->
            // Si ya existe una operación pendiente para este id, la reemplaza
            database.syncQueueQueries.deleteByEntityAndId(
                entity = SyncEntity.ROUTE,
                entity_id = entityId
            )
            database.syncQueueQueries.insert(
                entity = SyncEntity.ROUTE,
                entity_id = entityId,
                operation = operation,
                created_at = Clock.System.now()
            )
        }
    }

    suspend fun pushToApi(userRoute: UserRoute) {
        TODO("Not yet implemented")
    }

    suspend fun deleteFromApi(id: Long) {
        TODO("Not yet implemented")
    }

    suspend fun pullFromApi(userId: Long) {
        TODO("Not yet implemented")
    }
}
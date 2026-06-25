package com.routeplanner.app.features.notifier.data.local.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.routeplanner.app.features.common.data.database.DbHelper
import com.routeplanner.app.features.notifier.data.local.mapper.routeEntityMapper
import com.routeplanner.app.features.notifier.data.local.mapper.stopEntityMapper
import com.routeplanner.app.features.notifier.data.local.mapper.toNotifierRoute
import com.routeplanner.app.features.notifier.data.local.mapper.toNotifierRouteSummary
import com.routeplanner.app.features.notifier.data.local.mapper.toNotifierStop
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.data.remote.dto.NotifierRouteSync
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotifierRouteDao(
    private val dbHelper: DbHelper
) {
    suspend fun insertRoute(
        routeEntity: NotifierRouteEntity
    ): NotifierRoute {
        return dbHelper.withDatabase { database ->
            database.routeQueries.insert(
                userId = routeEntity.userId,
                stateId = routeEntity.stateId,
                name = routeEntity.name,
                createdAt = routeEntity.createdAt,
                originDir = routeEntity.originDir,
                originPlaceId = routeEntity.originPlaceId,
                originLatitude = routeEntity.originLatitude,
                originLongitude = routeEntity.originLongitude,
                destinationDir = routeEntity.destinationDir,
                destinationPlaceId = routeEntity.destinationPlaceId,
                destinationLatitude = routeEntity.destinationLatitude,
                destinationLongitude = routeEntity.destinationLongitude
            )
            val id = database.routeQueries.lastInsertRowId().executeAsOne()
            val stops = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .awaitAsList()
                .map { it.toNotifierStop() }
            database.routeQueries.selectRouteWithState(id).executeAsOne()
                .toNotifierRoute(stops)
        }
    }

    suspend fun updateRoute(
        notifierRouteItem: NotifierRoute,
        stateId: Long
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.update(
                id = notifierRouteItem.id,
                stateId = stateId,
                name = notifierRouteItem.name,
                originDir = notifierRouteItem.originDir,
                originPlaceId = notifierRouteItem.originPlaceId,
                originLatitude = notifierRouteItem.originLatitude,
                originLongitude = notifierRouteItem.originLongitude,
                destinationDir = notifierRouteItem.destinationDir,
                destinationPlaceId = notifierRouteItem.destinationPlaceId,
                destinationLatitude = notifierRouteItem.destinationLatitude,
                destinationLongitude = notifierRouteItem.destinationLongitude
            )
        }
    }

    suspend fun updateName(
        id: Long,
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
        id: Long,
        stateId: Long
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.updateState(
                id = id,
                stateId = stateId
            )
        }
    }

    suspend fun softDelete(
        id: Long
    ) {
        dbHelper.withDatabase { database ->
            database.routeQueries.softDelete(
                id = id
            )
        }
    }

    suspend fun selectAll(): List<NotifierRoute> {
        return dbHelper.withDatabase { database ->
            listOf()
        }
    }

    suspend fun observeRouteSummaries(): Flow<List<NotifierRouteSummary>> =
        dbHelper.withDatabase { db ->
            db.routeQueries.selectRouteSummaries()
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { it.map { route -> route.toNotifierRouteSummary() } }
        }

    suspend fun selectById(
        id: Long
    ): NotifierRoute {
        return dbHelper.withDatabase { database ->
            val stops = database.stopQueries
                .selectStopsWithDetailsByRouteId(id)
                .awaitAsList()
                .map { it.toNotifierStop() }
            val route = database.routeQueries.selectRouteWithState(id).executeAsOne()
            route.toNotifierRoute(stops)
        }
    }

    suspend fun selectByUserId(
        userId: Long
    ): List<NotifierRoute> {
        return dbHelper.withDatabase { database ->
            val state = database.routeStateQueries.selectAll().awaitAsList()
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
                }
        }
    }

    suspend fun selectPendingSync(): List<NotifierRoute> {
        return dbHelper.withDatabase { database ->
            val state = database.routeStateQueries.selectAll().awaitAsList()
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
            }
        }
    }

    suspend fun selectPendingDelete(): List<NotifierRoute> {
        return dbHelper.withDatabase { database ->
            val state = database.routeStateQueries.selectAll().awaitAsList()
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
            }
        }
    }

    suspend fun markAsSynced(id: Long) {
        dbHelper.withDatabase { database ->
            database.routeQueries.markAsSynced(id = id)
        }
    }

    suspend fun upsertFromApi(notifierRouteSync: NotifierRouteSync, userId: Long) {
        dbHelper.withDatabase { database ->
            val existing =
                database.routeQueries.selectById(notifierRouteSync.id).executeAsOneOrNull()
            when {
                existing == null -> database.routeQueries.insertFromApi(
                    id = notifierRouteSync.id,
                    userId = userId,
                    stateId = notifierRouteSync.stateId,
                    name = notifierRouteSync.name,
                    createdAt = notifierRouteSync.createdAt,
                    originDir = notifierRouteSync.originDir,
                    originPlaceId = notifierRouteSync.originPlaceId,
                    originLatitude = notifierRouteSync.originLatitude,
                    originLongitude = notifierRouteSync.originLongitude,
                    destinationDir = notifierRouteSync.destinationDir,
                    destinationPlaceId = notifierRouteSync.destinationPlaceId,
                    destinationLatitude = notifierRouteSync.destinationLatitude,
                    destinationLongitude = notifierRouteSync.destinationLongitude
                )   // registro nuevo
                existing.isSynced == 1L -> database.routeQueries.updateFromApi(
                    id = notifierRouteSync.id,
                    stateId = notifierRouteSync.stateId,
                    name = notifierRouteSync.name,
                    createdAt = notifierRouteSync.createdAt,
                    originDir = notifierRouteSync.originDir,
                    originPlaceId = notifierRouteSync.originPlaceId,
                    originLatitude = notifierRouteSync.originLatitude,
                    originLongitude = notifierRouteSync.originLongitude,
                    destinationDir = notifierRouteSync.destinationDir,
                    destinationPlaceId = notifierRouteSync.destinationPlaceId,
                    destinationLatitude = notifierRouteSync.destinationLatitude,
                    destinationLongitude = notifierRouteSync.destinationLongitude
                )  // pisa solo si está sincronizado
                // is_synced == 0 → tiene cambios locales pendientes, no se pisa
            }
        }
    }
}
package com.routeplanner.app.features.home.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.routeplanner.app.core.common.data.database.DbHelper
import com.routeplanner.app.core.utils.SyncEntity
import com.routeplanner.app.core.utils.generateId
import com.routeplanner.app.features.home.data.local.mapper.stopEntityMapper
import com.routeplanner.app.features.home.data.local.mapper.toUserStop
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum
import com.routeplanner.app.features.home.domain.model.StopStateEnum
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

class StopDao(
    private val dbHelper: DbHelper
) {
    fun observeStopsByRouteId(routeId: String): Flow<List<UserStop>> =
        dbHelper.withDatabaseFlow { database ->
            database.stopQueries
                .selectStopsWithDetailsByRouteId(routeId)
                .asFlow()
                .mapToList(Dispatchers.Default)
                .map { list -> list.map { it.toUserStop() } }
        }

    suspend fun insertStop(stop: UserStop, routeId: String): String {
        return dbHelper.withDatabase { database ->
            println("--onCreateStop--3")
            val newId = generateId()
            val order = database.stopQueries
                .selectByRouteId(routeId)
                .executeAsList()
                .size + 1
            println("--id: ${stop.id}")
            database.stopQueries.insert(
                id = stop.id,
                routeId = routeId,
                noticeId = StopNoticeEnum.fromName(stop.notice).id.toLong(),
                stateId = StopStateEnum.fromName(stop.state).id.toLong(),
                recipient = stop.recipient,
                direction = stop.direction,
                directionPlaceId = stop.directionPlaceId,
                latitude = stop.latitude,
                longitude = stop.longitude,
                orderNum = order.toLong(),
                note = stop.note
            )
            stop.id
        }
    }

    suspend fun updateStop(stop: UserStop) {
        dbHelper.withDatabase { database ->
            database.stopQueries.update(
                id = stop.id,
                noticeId = StopNoticeEnum.fromName(stop.notice).id.toLong(),
                stateId = StopStateEnum.fromName(stop.state).id.toLong(),
                recipient = stop.recipient,
                direction = stop.direction,
                directionPlaceId = stop.directionPlaceId,
                latitude = stop.latitude,
                longitude = stop.longitude,
                orderNum = stop.order.toLong(),
                note = stop.note
            )
        }
    }

    suspend fun updateState(id: String, stateId: Long) {
        dbHelper.withDatabase { database ->
            database.stopQueries.updateState(
                id = id,
                stateId = stateId
            )
        }
    }

    suspend fun softDelete(id: String) {
        dbHelper.withDatabase { database ->
            database.stopQueries.softDelete(id = id)
        }
    }

    suspend fun softDeleteByRouteId(routeId: String) {
        dbHelper.withDatabase { database ->
            database.stopQueries.softDeleteByRouteId(routeId = routeId)
        }
    }

    suspend fun selectById(id: String): UserStop {
        return dbHelper.withDatabase { database ->
            val stop = database.stopQueries.selectById(id).executeAsOneOrNull()
                ?: throw Exception("Stop not found")
            val stateDesc = StopStateEnum.fromId(stop.stopStateId.toInt()).description
            val noticeDesc = StopNoticeEnum.fromId(stop.noticeId.toInt()).description
            stop.stopEntityMapper(state = stateDesc, notice = noticeDesc)
        }
    }

    suspend fun selectByRouteId(routeId: String): List<UserStop> {
        return dbHelper.withDatabase { database ->
            database.stopQueries
                .selectStopsWithDetailsByRouteId(routeId)
                .executeAsList()
                .map { it.toUserStop() }
        }
    }

    suspend fun selectPendingSync(): List<UserStop> {
        return dbHelper.withDatabase { database ->
            database.stopQueries.selectPendingSync().executeAsList().map { stop ->
                val stateDesc = StopStateEnum.fromId(stop.stopStateId.toInt()).description
                val noticeDesc = StopNoticeEnum.fromId(stop.noticeId.toInt()).description
                stop.stopEntityMapper(state = stateDesc, notice = noticeDesc)
            }
        }
    }

    suspend fun selectByIdIncludingDeleted(id: String): UserStop {
        return dbHelper.withDatabase { database ->
            // selectById ya no filtra isDeleted, trae cualquier estado
            val stop = database.stopQueries.selectById(id).executeAsOneOrNull()
                ?: throw Exception("Stop not found")
            val stateDesc  = StopStateEnum.fromId(stop.stopStateId.toInt()).description
            val noticeDesc = StopNoticeEnum.fromId(stop.noticeId.toInt()).description
            stop.stopEntityMapper(
                state  = stateDesc,
                notice = noticeDesc
            )
        }
    }

    suspend fun selectPendingDelete(): List<UserStop> {
        return dbHelper.withDatabase { database ->
            database.stopQueries.selectPendingDelete().executeAsList().map { stop ->
                val stateDesc = StopStateEnum.fromId(stop.stopStateId.toInt()).description
                val noticeDesc = StopNoticeEnum.fromId(stop.noticeId.toInt()).description
                stop.stopEntityMapper(state = stateDesc, notice = noticeDesc)
            }
        }
    }

    suspend fun markAsSynced(id: String) {
        dbHelper.withDatabase { database ->
            database.stopQueries.markAsSynced(id = id)
        }
    }

    suspend fun deleteById(id: String) {
        dbHelper.withDatabase { database ->
            database.stopQueries.deleteById(id = id)
        }
    }

    suspend fun enqueueSyncOperation(entityId: String, operation: String) {
        println("--id: $entityId")
        dbHelper.withDatabase { database ->
            database.syncQueueQueries.deleteByEntityAndId(
                entity = SyncEntity.STOP,
                entity_id = entityId
            )
            database.syncQueueQueries.insert(
                entity = SyncEntity.STOP,
                entity_id = entityId,
                operation = operation,
                created_at = Clock.System.now()
            )
        }
    }

    suspend fun upsertFromApi(stop: UserStop, routeId: String) {
        dbHelper.withDatabase { database ->
            val existing = database.stopQueries.selectById(stop.id).executeAsOneOrNull()
            when {
                existing == null -> database.stopQueries.insertFromApi(
                    id = stop.id,
                    routeId = routeId,
                    noticeId = StopNoticeEnum.fromName(stop.notice).id.toLong(),
                    stateId = StopStateEnum.fromName(stop.state).id.toLong(),
                    recipient = stop.recipient,
                    direction = stop.direction,
                    directionPlaceId = stop.directionPlaceId,
                    latitude = stop.latitude,
                    longitude = stop.longitude,
                    orderNum = stop.order.toLong(),
                    note = stop.note
                )

                existing.isSynced == 1L -> database.stopQueries.updateFromApi(
                    id = stop.id,
                    routeId = routeId,
                    noticeId = StopNoticeEnum.fromName(stop.notice).id.toLong(),
                    stateId = StopStateEnum.fromName(stop.state).id.toLong(),
                    recipient = stop.recipient,
                    direction = stop.direction,
                    directionPlaceId = stop.directionPlaceId,
                    latitude = stop.latitude,
                    longitude = stop.longitude,
                    orderNum = stop.order.toLong(),
                    note = stop.note
                )
            }
        }
    }
}
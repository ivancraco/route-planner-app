package com.routeplanner.app.core.utils

import com.routeplanner.app.SyncQueue
import com.routeplanner.app.core.common.connectivity.ConnectivityObserver
import com.routeplanner.app.core.common.data.database.DbHelper
import com.routeplanner.app.features.home.domain.model.CreateRoute
import com.routeplanner.app.features.home.domain.model.RouteStateEnum
import com.routeplanner.app.features.home.domain.model.UpdateRoute
import com.routeplanner.app.features.home.domain.repository.RouteRepository
import com.routeplanner.app.features.home.domain.repository.StopRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.pow

class SyncManager(
    private val dbHelper: DbHelper,
    private val routeRepository: RouteRepository,
    private val stopRepository: StopRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope
) {
    private var syncJob: Job? = null

    fun start() {
        scope.launch {
            connectivityObserver.isConnected.collectLatest { connected ->
                if (connected) startSyncLoop() else stopSyncLoop()
            }
        }
    }

    fun stop() = stopSyncLoop()

    fun syncNowAsync() {
        scope.launch { syncNow() }
    }

    private suspend fun syncNow() {
        println("-----2-----")
        if (connectivityObserver.currentlyConnected()) {
            println("-----3-----")
            processQueue()
        }
    }

    private fun startSyncLoop() {
        if (syncJob?.isActive == true) return
        syncJob = scope.launch {
            while (isActive) {
                processQueue()
                delay(SyncConfig.SYNC_INTERVAL_MS)
            }
        }
    }

    private fun stopSyncLoop() {
        syncJob?.cancel()
        syncJob = null
    }

    private suspend fun processQueue() {
        // Pendientes de subir
        val pendingSync = dbHelper.withDatabase {
            it.syncQueueQueries.selectAll().executeAsList()
        }
        if (pendingSync.isEmpty()) {
            println("-----4-----")
            return
        }

        for (item in pendingSync) {
            if (item.retries >= SyncConfig.MAX_RETRIES) {
                dbHelper.withDatabase { it.syncQueueQueries.deleteById(item.id) }
                continue
            }
            println("-----5-----")
            val success = processItem(item)
            println("success: $success")
            if (!success) {
                val delay = minOf(
                    SyncConfig.BASE_DELAY_MS * (2.0.pow(item.retries.toInt())).toLong(),
                    SyncConfig.MAX_DELAY_MS
                )
                delay(delay)
            }
        }
    }

    private suspend fun processItem(item: SyncQueue): Boolean {
        return try {
            when (item.entity) {
                SyncEntity.ROUTE -> processRouteItem(item)
                SyncEntity.STOP -> processStopItem(item)
                else -> Unit
            }
            dbHelper.withDatabase { it.syncQueueQueries.deleteById(item.id) }
            true
        } catch (e: Exception) {
            println("error: ${e.stackTraceToString()}")
            dbHelper.withDatabase {
                it.syncQueueQueries.incrementRetry(
                    last_error = e.message,
                    id = item.id
                )
            }
            false
        }
    }

    private suspend fun processRouteItem(item: SyncQueue) {
        when (item.operation) {
            SyncOperation.INSERT -> {
                val route = routeRepository.selectById(item.entity_id)
                    ?: return
                routeRepository.insertRouteToApi(route)
                routeRepository.markAsSynced(item.entity_id)
            }
            SyncOperation.UPDATE -> {
                val route = routeRepository.selectById(item.entity_id)
                    ?: return
                routeRepository.updateRouteToApi(route)
                routeRepository.markAsSynced(item.entity_id)
            }
            SyncOperation.DELETE -> {
                routeRepository.deleteRouteToApi(item.entity_id)
                routeRepository.deletePermanently(item.entity_id)            }
        }
    }

    private suspend fun processStopItem(item: SyncQueue) {
        when (item.operation) {
            SyncOperation.INSERT,
            SyncOperation.UPDATE -> {
                val stop = stopRepository.selectById(item.entity_id)
                    ?: return
                stopRepository.pushToApi(stop, stop.routeId, item.operation)
                stopRepository.markAsSynced(item.entity_id)
            }
            SyncOperation.DELETE -> {
                // selectById sin filtrar isDeleted, para poder leer el routeId
                val stop = stopRepository.selectByIdIncludingDeleted(item.entity_id)
                    ?: return
                stopRepository.deleteFromApi(item.entity_id, stop.routeId)
                stopRepository.deletePermanently(item.entity_id) // recién acá se borra físico
            }
        }
    }
}
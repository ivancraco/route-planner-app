package com.routeplanner.app.core.utils

import com.routeplanner.app.SyncQueue
import com.routeplanner.app.SyncQueueQueries
import com.routeplanner.app.core.common.connectivity.ConnectivityObserver
import com.routeplanner.app.core.common.data.database.DbHelper
import com.routeplanner.app.features.home.domain.repository.NotifierRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.pow

class SyncManager(
    private val dbHelper: DbHelper,
    private val routeRepository: NotifierRepository,
    //private val stopRepository: StopRepository,
    private val connectivityObserver: ConnectivityObserver,
    private val scope: CoroutineScope
) {

    private var syncJob: Job? = null

    // Arranca el loop de sincronización, reacciona a cambios de conectividad
    fun start() {
        scope.launch {
            connectivityObserver.isConnected.collectLatest { connected ->
                if (connected) {
                    startSyncLoop()
                } else {
                    stopSyncLoop()
                }
            }
        }
    }

    fun stop() {
        stopSyncLoop()
    }

    // Permite forzar una sincronización inmediata (ej: al volver de background)
    suspend fun syncNow() {
        if (connectivityObserver.currentlyConnected()) {
            processQueue()
        }
    }

    // ─── Loop interno ──────────────────────────────────────────

    private fun startSyncLoop() {
        if (syncJob?.isActive == true) return
        syncJob = scope.launch {
            while (isActive) {
                processQueue()
                delay(SyncConfig.SYNC_INTERVAL_MS)
            }
        }
    }

    fun syncNowAsync() {
        scope.launch { syncNow() }
    }

    private fun stopSyncLoop() {
        syncJob?.cancel()
        syncJob = null
    }

    // ─── Procesa toda la cola en orden ────────────────────────

    private suspend fun processQueue() {
        val pending = dbHelper.withDatabase {  it.syncQueueQueries.selectAll().executeAsList() }
        if (pending.isEmpty()) return

        for (item in pending) {
            if (item.retries >= SyncConfig.MAX_RETRIES) {
                // Demasiados reintentos: se descarta para no bloquear la cola
                dbHelper.withDatabase {  it.syncQueueQueries.deleteById(item.id) }
                continue
            }
            val success = processItem(item)
            if (!success) {
                // Backoff exponencial: 2s, 4s, 8s, 16s, 32s (máx 60s)
                val delay = min(
                    SyncConfig.BASE_DELAY_MS * (2.0.pow(item.retries.toInt())).toLong(),
                    SyncConfig.MAX_DELAY_MS
                )
                delay(delay)
            }
        }
    }

    // ─── Procesa un ítem individual ───────────────────────────

    private suspend fun processItem(item: SyncQueue): Boolean {
        return try {
            when (item.entity) {
                SyncEntity.ROUTE -> processRouteItem(item)
                SyncEntity.STOP  -> processStopItem(item)
                else -> true // entidad desconocida, se descarta
            }
            dbHelper.withDatabase {  it.syncQueueQueries.deleteById(item.id) }
            true
        } catch (e: Exception) {
            dbHelper.withDatabase {  it.syncQueueQueries.incrementRetry(
                last_error = e.message,
                id = item.id
            )}
            false
        }
    }

    // ─── Operaciones por entidad ──────────────────────────────

    private suspend fun processRouteItem(item: SyncQueue) {
        when (item.operation) {
            SyncOperation.INSERT,
            SyncOperation.UPDATE -> {
                val route = routeRepository.getLocalById(item.entity_id)
                    ?: return // fue borrado localmente antes de sincronizar
                routeRepository.pushToApi(route)
                routeRepository.markAsSynced(item.entity_id)
            }
            SyncOperation.DELETE -> {
                routeRepository.deleteFromApi(item.entity_id)
                //routeRepository.deletePermanently(item.entity_id)
            }
        }
    }

    private suspend fun processStopItem(item: SyncQueue) {
        when (item.operation) {
            SyncOperation.INSERT,
            SyncOperation.UPDATE -> {
                /*val stop = stopRepository.getLocalById(item.entity_id.toInt())
                    ?: return
                stopRepository.pushToApi(stop)
                stopRepository.markAsSynced(item.entity_id.toInt())*/
            }
            SyncOperation.DELETE -> {
                /*stopRepository.deleteFromApi(item.entity_id.toInt())
                stopRepository.deletePermanently(item.entity_id.toInt())*/
            }
        }
    }
}
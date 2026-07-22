package com.routeplanner.app.core.utils

object SyncEntity {
    const val ROUTE = "Route"
    const val STOP = "Stop"
}

object SyncOperation {
    const val INSERT = "INSERT"
    const val UPDATE = "UPDATE"
    const val DELETE = "DELETE"
}

object SyncConfig {
    const val MAX_RETRIES = 5
    const val BASE_DELAY_MS = 2_000L   // 2 segundos
    const val MAX_DELAY_MS = 60_000L  // 1 minuto
    const val SYNC_INTERVAL_MS = 30_000L  // chequea la cola cada 30 segundos
}
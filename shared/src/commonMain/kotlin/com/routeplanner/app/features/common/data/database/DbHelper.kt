package com.routeplanner.app.features.common.data.database

import com.routeplanner.app.AppDatabase
import com.routeplanner.app.dbFactory.DatabaseFactory
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DbHelper(
    private val databaseFactory: DatabaseFactory
) {
    private var database: AppDatabase? = null
    private val mutex = Mutex()

    suspend fun <Result : Any> withDatabase(block: suspend (AppDatabase) -> Result) =
        mutex.withLock {
            database?.let { return@withLock block(it) }
            database = AppDatabase(databaseFactory.createDriver())
        }
}
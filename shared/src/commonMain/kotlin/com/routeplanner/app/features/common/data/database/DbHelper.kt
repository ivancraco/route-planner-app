package com.routeplanner.app.features.common.data.database

import com.routeplanner.app.AppDatabase
import com.routeplanner.app.Route
import com.routeplanner.app.dbFactory.DatabaseFactory
import com.routeplanner.app.features.common.data.database.adapter.instantAdapter
import com.routeplanner.app.features.common.data.database.adapter.stopsAdapter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DbHelper(
    private val databaseFactory: DatabaseFactory
) {
    private var database: AppDatabase? = null
    private val mutex = Mutex()

    suspend fun <Result : Any> withDatabase(block: suspend (AppDatabase) -> Result) =
        mutex.withLock {
            if (database == null) {
                database = AppDatabase(
                    databaseFactory.createDriver(),
                    Route.Adapter(instantAdapter))
            }
            return@withLock block(database!!)
        }
}
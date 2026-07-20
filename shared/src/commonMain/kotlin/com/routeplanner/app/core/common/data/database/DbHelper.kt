package com.routeplanner.app.core.common.data.database

import com.routeplanner.app.AppDatabase
import com.routeplanner.app.Route
import com.routeplanner.app.SyncQueue
import com.routeplanner.app.core.common.data.database.adapter.instantAdapter
import com.routeplanner.app.core.dbFactory.DatabaseFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DbHelper(
    private val databaseFactory: DatabaseFactory
) {
    private val mutex = Mutex()
    private var database: AppDatabase? = null

    // Para queries/updates puntuales (suspend)
    suspend fun <Result : Any> withDatabase(block: suspend (AppDatabase) -> Result) =
        mutex.withLock {
            block(getOrCreateDatabase())
        }

    // Para queries reactivas (Flow)
    fun <Result> withDatabaseFlow(
        block: (AppDatabase) -> Flow<Result>
    ): Flow<Result> = flow {
        val db = mutex.withLock { getOrCreateDatabase() }
        emitAll(block(db))
    }

    private suspend fun getOrCreateDatabase(): AppDatabase {
        if (database == null) {
            database = AppDatabase(
                databaseFactory.createDriver(),
                Route.Adapter(instantAdapter),
                SyncQueue.Adapter(instantAdapter),
            )
        }
        return database!!
    }
    /*
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
        }*/
}
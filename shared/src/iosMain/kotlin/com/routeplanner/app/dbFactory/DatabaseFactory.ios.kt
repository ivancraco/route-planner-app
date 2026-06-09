package com.routeplanner.app.dbFactory

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.routeplanner.app.AppDatabase

actual class DatabaseFactory {
    actual suspend fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            AppDatabase.Schema.synchronous(),
            DB_FILE_NAME
        )
    }
}
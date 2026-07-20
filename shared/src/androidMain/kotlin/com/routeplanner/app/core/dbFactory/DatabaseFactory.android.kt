package com.routeplanner.app.core.dbFactory

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.routeplanner.app.AppDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual suspend fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            AppDatabase.Schema.synchronous(),
            context,
            DB_FILE_NAME
        )
    }
}
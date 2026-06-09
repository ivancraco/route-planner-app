package com.routeplanner.app.dbFactory

import app.cash.sqldelight.db.SqlDriver

const val DB_FILE_NAME = "route-planner-app.db"

expect class DatabaseFactory {
    suspend fun createDriver(): SqlDriver
}
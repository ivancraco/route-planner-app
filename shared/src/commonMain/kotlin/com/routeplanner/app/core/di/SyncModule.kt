package com.routeplanner.app.core.di

import app.cash.sqldelight.db.SqlDriver
import com.routeplanner.app.AppDatabase
import com.routeplanner.app.core.common.connectivity.ConnectivityObserver
import com.routeplanner.app.core.common.connectivity.KmpConnectivityObserver
import com.routeplanner.app.core.dbFactory.DatabaseFactory
import com.routeplanner.app.core.utils.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val syncModule = module {
    single<ConnectivityObserver> { KmpConnectivityObserver() }

    single {
        SyncManager(
            dbHelper = get(),
            routeRepository = get(),
            //stopRepository = get(),
            connectivityObserver = get(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        )
    }
}
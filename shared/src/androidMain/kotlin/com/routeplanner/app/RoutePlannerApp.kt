package com.routeplanner.app

import android.app.Application
import com.routeplanner.app.core.dbFactory.DatabaseFactory
import com.routeplanner.app.core.di.initKoin
import com.routeplanner.app.core.di.settingsModule
import com.routeplanner.app.core.di.syncModule
import com.routeplanner.app.core.utils.SyncManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

class RoutePlannerApp : Application() {
    private val androidModules = module {
        single { DatabaseFactory(applicationContext) }
    }

    override fun onCreate() {
        super.onCreate()
        initKoinAndroid()
        // arranca el loop de sync
        KoinPlatform.getKoin().get<SyncManager>().start()
    }

    private fun initKoinAndroid() {
        initKoin(additionalModules = listOf(androidModules, syncModule, settingsModule)) {
            androidContext(applicationContext)
        }
    }
}
package com.routeplanner.app

import android.app.Application
import com.routeplanner.app.core.dbFactory.DatabaseFactory
import com.routeplanner.app.core.di.initKoin
import com.routeplanner.app.core.di.syncModule
import com.routeplanner.app.core.utils.SyncManager
import com.routeplanner.app.features.home.domain.repository.NotifierRepository
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.compose.koinInject
import org.koin.core.context.GlobalContext.get
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
        initKoin(additionalModules = listOf(androidModules, syncModule)) {
            androidContext(applicationContext)
        }
    }
}
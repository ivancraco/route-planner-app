package com.routeplanner.app

import android.app.Application
import com.routeplanner.app.dbFactory.DatabaseFactory
import com.routeplanner.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class RoutePlannerApp : Application() {
    private val androidModules = module {
        single { DatabaseFactory(applicationContext) }
    }

    override fun onCreate() {
        super.onCreate()
        initKoinAndroid()
    }

    private fun initKoinAndroid() {
        initKoin(additionalModules = listOf(androidModules)) {
            androidContext(applicationContext)
        }
    }
}
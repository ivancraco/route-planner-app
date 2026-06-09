package com.routeplanner.app

import com.routeplanner.app.dbFactory.DatabaseFactory
import com.routeplanner.app.di.initKoin
import org.koin.dsl.module

val iosModules = module {
    single { DatabaseFactory() }
}

fun initKoinIOS() = initKoin(additionalModules = listOf(iosModules))
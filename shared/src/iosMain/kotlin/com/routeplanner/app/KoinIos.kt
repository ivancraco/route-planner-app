package com.routeplanner.app

import com.routeplanner.app.core.dbFactory.DatabaseFactory
import com.routeplanner.app.core.di.initKoin
import org.koin.dsl.module

val iosModules = module {
    single { DatabaseFactory() }
}

fun initKoinIOS() = initKoin(additionalModules = listOf(iosModules))
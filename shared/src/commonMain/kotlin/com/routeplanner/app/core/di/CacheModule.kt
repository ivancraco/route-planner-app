package com.routeplanner.app.core.di

import com.routeplanner.app.core.common.data.database.DbHelper
import com.routeplanner.app.features.home.data.local.dao.NotifierRouteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

fun cacheModule() = module {
    single<CoroutineContext> { Dispatchers.Default }
    single { CoroutineScope(get()) }
    single { DbHelper(get()) }
    single { NotifierRouteDao(get()) }
}
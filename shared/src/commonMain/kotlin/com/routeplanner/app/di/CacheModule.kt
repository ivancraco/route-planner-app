package com.routeplanner.app.di

import com.routeplanner.app.features.common.data.database.DbHelper
import com.routeplanner.app.features.common.data.database.dao.RouteDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

fun cacheModule() = module {
    single<CoroutineContext> { Dispatchers.Default }
    single { CoroutineScope(get()) }
    single { DbHelper(get()) }
    single { RouteDao(get()) }
}
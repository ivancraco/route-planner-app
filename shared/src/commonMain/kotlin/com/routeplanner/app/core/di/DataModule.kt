package com.routeplanner.app.core.di

import com.routeplanner.app.features.home.data.local.datasource.NotifierLocalDataSource
import com.routeplanner.app.features.home.data.local.datasource.NotifierLocalDataSourceImpl
import com.routeplanner.app.features.home.data.remote.datasource.NotifierRemoteDataSource
import com.routeplanner.app.features.home.data.remote.datasource.NotifierRemoteDataSourceImpl
import com.routeplanner.app.features.home.data.repository.NotifierRepositoryImpl
import com.routeplanner.app.features.home.domain.repository.NotifierRepository
import org.koin.dsl.module

fun dataModule() = module {
    single<NotifierLocalDataSource> { NotifierLocalDataSourceImpl(get()) }
    single<NotifierRemoteDataSource> { NotifierRemoteDataSourceImpl(get()) }
    single<NotifierRepository> { NotifierRepositoryImpl(get(), get()) }
}

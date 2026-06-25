package com.routeplanner.app.di

import com.routeplanner.app.features.notifier.data.feed.datasource.NotifierLocalDataSource
import com.routeplanner.app.features.notifier.data.feed.datasource.NotifierLocalDataSourceImpl
import com.routeplanner.app.features.notifier.data.repository.NotifierRepositoryImpl
import com.routeplanner.app.features.notifier.domain.repository.NotifierRepository
import org.koin.dsl.module

fun dataModule() = module {
    single<NotifierLocalDataSource> { NotifierLocalDataSourceImpl(get()) }
    // Cuando se agregue NotifierRemoteDataSource se debe agregar segundo get() aquí
    single<NotifierRepository> { NotifierRepositoryImpl(get()) }
}

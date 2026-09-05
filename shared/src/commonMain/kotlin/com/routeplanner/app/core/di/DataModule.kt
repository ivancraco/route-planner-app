package com.routeplanner.app.core.di

import com.routeplanner.app.core.ApiKeys
import com.routeplanner.app.features.home.data.local.dao.StopDao
import com.routeplanner.app.features.home.data.local.datasource.RouteLocalDataSource
import com.routeplanner.app.features.home.data.local.datasource.RouteLocalDataSourceImpl
import com.routeplanner.app.features.home.data.local.datasource.StopLocalDataSource
import com.routeplanner.app.features.home.data.local.datasource.StopLocalDataSourceImpl
import com.routeplanner.app.features.home.data.remote.api.ReverseGeocodingApi
import com.routeplanner.app.features.home.data.remote.api.RoutesApi
import com.routeplanner.app.features.home.data.remote.datasource.RouteRemoteDataSource
import com.routeplanner.app.features.home.data.remote.datasource.RouteRemoteDataSourceImpl
import com.routeplanner.app.features.home.data.remote.datasource.StopRemoteDataSource
import com.routeplanner.app.features.home.data.remote.datasource.StopRemoteDataSourceImpl
import com.routeplanner.app.features.home.data.repository.RouteRepositoryImpl
import com.routeplanner.app.features.home.data.repository.StopRepositoryImpl
import com.routeplanner.app.features.home.domain.repository.RouteRepository
import com.routeplanner.app.features.home.domain.repository.StopRepository
import org.koin.dsl.module

fun dataModule() = module {
    single { StopDao(get()) }
    single<RouteLocalDataSource> { RouteLocalDataSourceImpl(get()) }
    single<RouteRemoteDataSource> { RouteRemoteDataSourceImpl(get()) }
    single { ReverseGeocodingApi(get(ExternalClient), ApiKeys.GOOGLE_API_KEY) }
    single { RoutesApi(get(ExternalClient), ApiKeys.GOOGLE_API_KEY) }
    single<RouteRepository> {
        RouteRepositoryImpl(
            routeLocalDataSource = get(),
            routeRemoteDataSource = get(),
            syncManager = inject(),  // ← lazy para romper ciclo
            reverseGeocodingApi = get(),
            optimalRouteApi = get()
        )
    }
    single<StopLocalDataSource> { StopLocalDataSourceImpl(get()) }
    single<StopRemoteDataSource> { StopRemoteDataSourceImpl(get()) }
    single<StopRepository> {
        StopRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            syncManager = inject()
        )
    }
}

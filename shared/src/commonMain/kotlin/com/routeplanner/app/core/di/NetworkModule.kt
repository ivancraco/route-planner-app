package com.routeplanner.app.core.di

import com.routeplanner.app.core.common.data.api.httpCliente
import com.routeplanner.app.features.home.data.remote.api.NotifierApiService
import com.routeplanner.app.features.home.data.remote.api.NotifierApiServiceImpl
import io.ktor.client.HttpClient
import org.koin.dsl.module

fun networkModule() = module {
    single<HttpClient> {
        httpCliente
    }
    single<NotifierApiService> { NotifierApiServiceImpl(get()) }
}
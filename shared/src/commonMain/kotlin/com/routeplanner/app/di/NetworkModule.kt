package com.routeplanner.app.di

import com.routeplanner.app.features.common.data.api.httpCliente
import io.ktor.client.HttpClient
import org.koin.dsl.module

fun networkModule() = module {
    single<HttpClient> {
        httpCliente
    }
}
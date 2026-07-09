package com.routeplanner.app.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoin(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {},
) = startKoin {
    appDeclaration()
    modules(additionalModules + cacheModule() + dataModule() + networkModule() + placesModule() + viewModelModule())
}
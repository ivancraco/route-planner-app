package com.routeplanner.app.core.di

import com.routeplanner.app.features.home.presentation.NotifierViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun viewModelModule() = module {
    viewModel {
        NotifierViewModel(
            routeRepository = get(),
            stopRepository = get(),
            sessionLocalDataSource = get(),
            repository = get()
            //locationBiasProvider = { null }
        )
    }
}
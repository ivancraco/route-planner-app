package com.routeplanner.app.di

import com.routeplanner.app.features.notifier.presentation.NotifierViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.text.get

fun viewModelModule() = module {
    viewModel {
        NotifierViewModel(
            notifierRepository = get(),
            repository = get(),
            locationBiasProvider = { null })
    }
}
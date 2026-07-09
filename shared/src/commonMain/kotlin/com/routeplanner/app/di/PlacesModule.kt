package com.routeplanner.app.di

import com.routeplanner.app.features.notifier.data.remote.api.PlacesAutocompleteApi
import com.routeplanner.app.features.notifier.data.remote.api.PlacesDetailsApi
import com.routeplanner.app.features.notifier.data.repository.AddressAutocompleteRepositoryImpl
import com.routeplanner.app.features.notifier.domain.repository.AddressAutocompleteRepository
import com.routeplanner.app.features.notifier.places.PlacesSessionTokenProvider
import org.koin.dsl.module

data class PlacesApiKey(val value: String)

fun placesModule() = module {
    single { PlacesApiKey("") }
    single { PlacesAutocompleteApi(get(), get<PlacesApiKey>().value) }
    single { PlacesDetailsApi(get(), get<PlacesApiKey>().value) }
    single { PlacesSessionTokenProvider() }
    single<AddressAutocompleteRepository> { AddressAutocompleteRepositoryImpl(get(), get(), get()) }
}
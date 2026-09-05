package com.routeplanner.app.core.di

import com.routeplanner.app.core.ApiKeys
import com.routeplanner.app.features.home.data.remote.api.PlacesAutocompleteApi
import com.routeplanner.app.features.home.data.remote.api.PlacesDetailsApi
import com.routeplanner.app.features.home.data.repository.AddressAutocompleteRepositoryImpl
import com.routeplanner.app.features.home.domain.repository.AddressAutocompleteRepository
import com.routeplanner.app.features.home.places.PlacesSessionTokenProvider
import org.koin.dsl.module

data class PlacesApiKey(val value: String)

fun placesModule() = module {
    single { PlacesApiKey(ApiKeys.GOOGLE_API_KEY) }
    single { PlacesAutocompleteApi(get(ExternalClient), get<PlacesApiKey>().value) }
    single { PlacesDetailsApi(get(ExternalClient), get<PlacesApiKey>().value) }
    single { PlacesSessionTokenProvider() }
    single<AddressAutocompleteRepository> { AddressAutocompleteRepositoryImpl(get(), get(), get()) }
}
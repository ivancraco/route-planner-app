package com.routeplanner.app.features.home.domain.repository

import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.LocationBias
import com.routeplanner.app.features.home.places.SelectedAddress

interface AddressAutocompleteRepository {
    suspend fun search(query: String, bias: LocationBias?): Result<List<AddressSuggestion>>
    suspend fun resolve(placeId: String): Result<SelectedAddress>
}
package com.routeplanner.app.features.notifier.domain.repository

import com.routeplanner.app.features.notifier.places.AddressSuggestion
import com.routeplanner.app.features.notifier.places.LocationBias
import com.routeplanner.app.features.notifier.places.SelectedAddress

interface AddressAutocompleteRepository {
    suspend fun search(query: String, bias: LocationBias?): Result<List<AddressSuggestion>>
    suspend fun resolve(placeId: String): Result<SelectedAddress>
}
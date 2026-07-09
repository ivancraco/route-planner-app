package com.routeplanner.app.features.notifier.data.repository

import com.routeplanner.app.features.notifier.data.remote.api.PlacesAutocompleteApi
import com.routeplanner.app.features.notifier.data.remote.api.PlacesDetailsApi
import com.routeplanner.app.features.notifier.domain.repository.AddressAutocompleteRepository
import com.routeplanner.app.features.notifier.places.AddressSuggestion
import com.routeplanner.app.features.notifier.places.LocationBias
import com.routeplanner.app.features.notifier.places.PlacePredictionDto
import com.routeplanner.app.features.notifier.places.PlacesSessionTokenProvider
import com.routeplanner.app.features.notifier.places.SelectedAddress

class AddressAutocompleteRepositoryImpl(
    private val autocompleteApi: PlacesAutocompleteApi,
    private val detailsApi: PlacesDetailsApi,
    private val sessionTokenProvider: PlacesSessionTokenProvider
) : AddressAutocompleteRepository {

    override suspend fun search(query: String, bias: LocationBias?): Result<List<AddressSuggestion>> {
        val token = sessionTokenProvider.current()
        return autocompleteApi.autocomplete(query, token, bias).map { predictions ->
            predictions.map { it.toDomain() }
        }
    }

    override suspend fun resolve(placeId: String): Result<SelectedAddress> {
        val token = sessionTokenProvider.current()
        return detailsApi.getDetails(placeId, token).map { details ->
            sessionTokenProvider.reset()
            SelectedAddress(
                placeId = details.id,
                formattedAddress = details.formattedAddress.orEmpty(),
                latitude = details.location?.latitude ?: 0.0,
                longitude = details.location?.longitude ?: 0.0
            )
        }
    }

    private fun PlacePredictionDto.toDomain() = AddressSuggestion(
        placeId = placeId,
        primaryText = structuredFormat?.mainText?.text ?: text.text,
        secondaryText = structuredFormat?.secondaryText?.text,
        fullText = text.text
    )
}
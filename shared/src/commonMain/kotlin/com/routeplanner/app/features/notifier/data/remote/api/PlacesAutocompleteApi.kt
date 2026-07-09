package com.routeplanner.app.features.notifier.data.remote.api

import com.routeplanner.app.features.notifier.places.AutocompleteRequest
import com.routeplanner.app.features.notifier.places.AutocompleteResponse
import com.routeplanner.app.features.notifier.places.LocationBias
import com.routeplanner.app.features.notifier.places.PlacePredictionDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class PlacesAutocompleteApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    suspend fun autocomplete(
        input: String,
        sessionToken: String,
        locationBias: LocationBias? = null
    ): Result<List<PlacePredictionDto>> = runCatching {
        val response = httpClient.post("https://places.googleapis.com/v1/places:autocomplete") {
            contentType(ContentType.Application.Json)
            header("X-Goog-Api-Key", apiKey)
            setBody(
                AutocompleteRequest(
                    input = input,
                    locationBias = locationBias,
                    sessionToken = sessionToken
                )
            )
        }.body<AutocompleteResponse>()
        response.suggestions.mapNotNull { it.placePrediction }
    }
}
package com.routeplanner.app.features.notifier.data.remote.api

import com.routeplanner.app.features.notifier.places.PlaceDetailsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

class PlacesDetailsApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    suspend fun getDetails(placeId: String, sessionToken: String): Result<PlaceDetailsResponse> = runCatching {
        httpClient.get("https://places.googleapis.com/v1/places/$placeId") {
            header("X-Goog-Api-Key", apiKey)
            header("X-Goog-FieldMask", "id,formattedAddress,location")
            parameter("sessionToken", sessionToken)
        }.body()
    }
}
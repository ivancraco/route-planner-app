package com.routeplanner.app.features.home.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ReverseGeocodingApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    suspend fun getAddress(
        latitude: Double,
        longitude: Double
    ): Result<String> = runCatching {
        val response = httpClient.get(
            "https://maps.googleapis.com/maps/api/geocode/json"
        ) {
            parameter("latlng", "$latitude,$longitude")
            parameter("key", apiKey)
            parameter("language", "es")
            parameter("result_type", "street_address|route")
        }.body<GeocodingResponse>()

        response.results.firstOrNull()?.formattedAddress
            ?: throw Exception("No se encontró dirección")
    }
}

@Serializable
data class GeocodingResponse(
    @SerialName("results") val results: List<GeocodingResult>,
    @SerialName("status")  val status: String
)

@Serializable
data class GeocodingResult(
    @SerialName("formatted_address") val formattedAddress: String,
    @SerialName("place_id")          val placeId: String
)
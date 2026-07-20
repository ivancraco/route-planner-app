package com.routeplanner.app.features.home.places

import kotlinx.serialization.Serializable

@Serializable
data class AutocompleteRequest(
    val input: String,
    val locationBias: LocationBias? = null,
    val sessionToken: String
)

@Serializable
data class LocationBias(val circle: Circle)

@Serializable
data class Circle(val center: LatLngDto, val radius: Double)

@Serializable
data class LatLngDto(val latitude: Double, val longitude: Double)

@Serializable
data class AutocompleteResponse(
    val suggestions: List<SuggestionDto> = emptyList()
)

@Serializable
data class SuggestionDto(val placePrediction: PlacePredictionDto? = null)

@Serializable
data class PlacePredictionDto(
    val placeId: String,
    val text: FormattedTextDto,
    val structuredFormat: StructuredFormatDto? = null
)

@Serializable
data class FormattedTextDto(val text: String)

@Serializable
data class StructuredFormatDto(
    val mainText: FormattedTextDto,
    val secondaryText: FormattedTextDto? = null
)
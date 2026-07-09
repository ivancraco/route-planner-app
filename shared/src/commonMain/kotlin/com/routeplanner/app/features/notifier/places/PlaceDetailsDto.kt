package com.routeplanner.app.features.notifier.places

import kotlinx.serialization.Serializable

@Serializable
data class PlaceDetailsResponse(
    val id: String,
    val formattedAddress: String? = null,
    val location: LatLngDto? = null
)
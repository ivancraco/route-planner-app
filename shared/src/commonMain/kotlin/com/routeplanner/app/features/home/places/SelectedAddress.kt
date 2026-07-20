package com.routeplanner.app.features.home.places

data class SelectedAddress(
    val placeId: String,
    val formattedAddress: String,
    val latitude: Double,
    val longitude: Double
)
package com.routeplanner.app.features.home.places

data class AddressSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String?,
    val fullText: String
)
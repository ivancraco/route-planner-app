package com.routeplanner.app.features.notifier.places

data class AddressSuggestion(
    val placeId: String,
    val primaryText: String,
    val secondaryText: String?,
    val fullText: String
)
package com.routeplanner.app.features.notifier.domain.model

import com.routeplanner.app.features.notifier.places.AddressSuggestion

data class AddressSearchState(
    val query: String = "",
    val suggestions: List<AddressSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

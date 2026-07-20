package com.routeplanner.app.features.home.domain.model

import com.routeplanner.app.features.home.places.AddressSuggestion

data class AddressSearchState(
    val query: String = "",
    val suggestions: List<AddressSuggestion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

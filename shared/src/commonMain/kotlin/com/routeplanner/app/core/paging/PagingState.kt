package com.routeplanner.app.core.paging

data class PagingState<T>(
    val items: List<T> = emptyList(),
    val currentPage: Int = 1,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val endReached: Boolean = false,
    val error: Throwable? = null,
)

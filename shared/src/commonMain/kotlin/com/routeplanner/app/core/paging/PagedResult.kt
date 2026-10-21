package com.routeplanner.app.core.paging

data class PagedResult<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Long,
    val totalPages: Int
) {
    val endReached: Boolean get() = page >= totalPages
}

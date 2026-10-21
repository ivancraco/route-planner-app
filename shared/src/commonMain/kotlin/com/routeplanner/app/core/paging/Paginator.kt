package com.routeplanner.app.core.paging

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlin.time.Duration.Companion.milliseconds

class Paginator<T>(
    private val firstPage: Int = 1,
    private val pageSize: Int = 30,
    private val onLoad: suspend (page: Int, pageSize: Int) -> PagedResult<T>
) {
    private val _state = MutableStateFlow(PagingState<T>())
    val state: StateFlow<PagingState<T>> = _state.asStateFlow()
    private val loadMutex = Mutex()

    suspend fun loadNextPage() {
        val current = _state.value
        if (current.isLoading || current.isRefreshing || current.endReached) return
        if (!loadMutex.tryLock()) return

        try {
            _state.update { it.copy(isLoading = true, error = null) }
            val nextPage = if (current.items.isEmpty()) firstPage else current.currentPage + 1

            runCatching {
                onLoad(nextPage, pageSize)
            }
                .onSuccess { result ->
                    _state.update {
                        it.copy(
                            items = it.items + result.items,
                            currentPage = result.page,
                            endReached = result.endReached,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, error = e) }
                }
        }
        finally {
            loadMutex.unlock()
        }
    }

    /** Pull-to-refresh: vuelve a pedir la primera página y reemplaza el listado. */
    suspend fun refresh() {
        if (!loadMutex.tryLock()) return
        try {
            _state.update { it.copy(isRefreshing = true, error = null) }
            runCatching { onLoad(firstPage, pageSize) }
                .onSuccess { result ->
                    _state.value = PagingState(
                        items = result.items,
                        currentPage = result.page,
                        endReached = result.endReached,
                    )
                }
                .onFailure { e ->
                    _state.update { it.copy(isRefreshing = false, error = e) }
                }
        } finally {
            loadMutex.unlock()
        }
    }

    fun reset() {
        _state.value = PagingState()
    }
}
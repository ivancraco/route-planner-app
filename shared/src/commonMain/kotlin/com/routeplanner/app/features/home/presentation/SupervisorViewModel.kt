package com.routeplanner.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routeplanner.app.core.paging.Paginator
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

sealed class SupervisorRouteState {
    object Idle : SupervisorRouteState()
    object Loading : SupervisorRouteState()
    data class Success(val data: List<SupervisorRouteSummary>) : SupervisorRouteState()
    data class Error(val message: String) : SupervisorRouteState()
}

class SupervisorViewModel(
    val routeRepository: RouteRepository
): ViewModel() {

    var username: String? = null
        private set

    private val _paginator = Paginator { page, pageSize ->
        routeRepository.getPage(
            username = username,
            page = page,
            pageSize = pageSize
        )
    }

    val state = _paginator.state

    private val _supervisorRouteState =
        MutableStateFlow<SupervisorRouteState>(SupervisorRouteState.Idle)

    val supervisorRouteState = _supervisorRouteState.asStateFlow()

    var routeSummarySelected: SupervisorRouteSummary? = null
        private set

    init {
        //getAllRoutesFromApi()
        loadNextPage()
    }

    fun loadNextPage() {
        viewModelScope.launch(Dispatchers.IO) {
            _paginator.loadNextPage()
        }
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _paginator.refresh()
        }
    }

    fun setUsername(query: String?) {
        username = query
    }

    fun setRouteSummary(route: SupervisorRouteSummary?) {
        routeSummarySelected = route
    }
}
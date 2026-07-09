package com.routeplanner.app.features.notifier.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.domain.model.AddressSearchState
import com.routeplanner.app.features.notifier.domain.model.NotifierRoute
import com.routeplanner.app.features.notifier.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.notifier.domain.model.RouteStateEnum
import com.routeplanner.app.features.notifier.domain.repository.AddressAutocompleteRepository
import com.routeplanner.app.features.notifier.domain.repository.NotifierRepository
import com.routeplanner.app.features.notifier.places.AddressSuggestion
import com.routeplanner.app.features.notifier.places.LocationBias
import com.routeplanner.app.features.notifier.places.SelectedAddress
import com.routeplanner.app.features.notifier.presentation.navigation.AddressType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class NotifierRouteState() {
    object Idle : NotifierRouteState()
    object Loading : NotifierRouteState()
    data class Success(val data: List<NotifierRoute>) : NotifierRouteState()
    data class Error(val message: String) : NotifierRouteState()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NotifierViewModel(
    private val notifierRepository: NotifierRepository,
    private val repository: AddressAutocompleteRepository,
    private val locationBiasProvider: suspend () -> LocationBias?
) : ViewModel() {

    var placesState = MutableStateFlow(AddressSearchState())
        private set
    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(300)
            .map { it.trim() }
            .distinctUntilChanged()
            .onEach { q ->
                if (q.length < 3) placesState.update {
                    it.copy(
                        suggestions = emptyList(),
                        isLoading = false
                    )
                }
            }
            .filter { it.length >= 3 }
            .onEach { placesState.update { it.copy(isLoading = true, error = null) } }
            .mapLatest { q -> repository.search(q, locationBiasProvider()) }
            .onEach { result ->
                result.fold(
                    onSuccess = { suggestions ->
                        placesState.update {
                            it.copy(
                                suggestions = suggestions,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { e ->
                        placesState.update {
                            it.copy(
                                isLoading = false,
                                error = e.message
                            )
                        }
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        placesState.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    fun onSuggestionSelected(suggestion: AddressSuggestion, onResolved: (SelectedAddress) -> Unit) {
        viewModelScope.launch {
            placesState.update { it.copy(isLoading = true) }
            repository.resolve(suggestion.placeId).fold(
                onSuccess = { selected ->
                    placesState.update {
                        it.copy(
                            suggestions = emptyList(),
                            isLoading = false
                        )
                    }
                    onResolved(selected)
                },
                onFailure = { e ->
                    placesState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                }
            )
        }
    }

    fun clear() {
        queryFlow.value = ""
        placesState.update { AddressSearchState() }
    }

    private val _selectedRouteId = MutableStateFlow<Long?>(null)
    val route: StateFlow<NotifierRoute?> = _selectedRouteId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                notifierRepository.observeRoute(id)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    var routeSummary: MutableStateFlow<List<NotifierRouteSummary>> = MutableStateFlow(listOf())
        private set
    var routes: MutableStateFlow<List<NotifierRoute>> = MutableStateFlow(emptyList())
        private set

    fun selectRoute(id: Long) {
        _selectedRouteId.value = id
    }

    fun clearRoute() {
        _selectedRouteId.value = null
    }
    fun createRoute(routeEntity: NotifierRouteEntity) {
        viewModelScope.launch(Dispatchers.Default) {
            val id = notifierRepository.insertRoute(routeEntity)
            selectRoute(id)
            //route.value = result
        }
    }

    fun getAllRoutes() {
        viewModelScope.launch(Dispatchers.Default) {
            val pepe = notifierRepository.selectAll()
            if (pepe.isSuccess) {
                routes.value = pepe.getOrDefault(emptyList())
            }
        }
    }

    fun getAll() {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.observeRouteSummaries().collectLatest {
                routeSummary.value = it
            }
        }
    }

    fun getById(routeId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = notifierRepository.observeRoute(routeId)
            /*if (result.isSuccess) {
                route.value = result.getOrDefault(null)
            }*/
        }
    }

    fun updateRoute(notifierRoute: NotifierRoute) {
        val routeStateEnumId = RouteStateEnum.fromName(notifierRoute.state).id
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.updateRoute(notifierRoute)
        }
    }

    fun updateRouteName(id: Long, name: String) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.updateName(id, name)
        }
    }

    /*fun updateRouteOrigin(
        id: Long,
        originDir: String,
        originLat: Double,
        originLng: Double
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.updateRouteOrigin(
                id,
                originDir,
                originLat,
                originLng
            )
        }
    }

    fun updateRouteDestination(
        id: Long,
        destinationDir: String,
        destinationLat: Double,
        destinationLng: Double
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.updateRouteDestination(
                id,
                destinationDir,
                destinationLat,
                destinationLng
            )
        }
    }*/

    fun deleteRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            notifierRepository.softDelete(routeId)
            clearRoute()
            //route.value = null
        }
    }
}
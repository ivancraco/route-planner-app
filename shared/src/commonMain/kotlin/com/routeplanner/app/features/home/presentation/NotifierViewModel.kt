package com.routeplanner.app.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.routeplanner.app.core.utils.PolylineDecoder
import com.routeplanner.app.core.utils.generateId
import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.home.domain.model.AddressSearchState
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.RouteStateEnum
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.StopStateEnum
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.domain.repository.AddressAutocompleteRepository
import com.routeplanner.app.features.home.domain.repository.RouteRepository
import com.routeplanner.app.features.home.domain.repository.StopRepository
import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.Circle
import com.routeplanner.app.features.home.places.LatLngDto
import com.routeplanner.app.features.home.places.LocationBias
import com.routeplanner.app.features.home.places.SelectedAddress
import com.swmansion.kmpmaps.core.Coordinates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant

sealed class NotifierRouteState() {
    object Idle : NotifierRouteState()
    object Loading : NotifierRouteState()
    data class Success(val data: List<UserRoute>) : NotifierRouteState()
    data class Error(val message: String) : NotifierRouteState()
}

data class CreateRouteFormState(
    val name: String = "",
    val originDir: String = "",
    val originPlaceId: String? = null,
    val originLatitude: Double = 0.0,
    val originLongitude: Double = 0.0,
    val selectedDate: Instant = Clock.System.now(),
    val isResolvingOrigin: Boolean = false,
    val isOptimizing: Boolean = false,
    val error: String? = null
) {
    val isValid get() = name.isNotBlank() && originDir.isNotBlank()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NotifierViewModel(
    private val routeRepository: RouteRepository,
    private val stopRepository: StopRepository,
    private val sessionLocalDataSource: SessionLocalDataSource,
    private val repository: AddressAutocompleteRepository,
    //private val locationBiasProvider: suspend () -> LocationBias?
) : ViewModel() {

    var placesState = MutableStateFlow(AddressSearchState())
        private set
    private val queryFlow = MutableStateFlow("")

    private val _stopFormState = MutableStateFlow(StopFormState())
    val stopFormState = _stopFormState.asStateFlow()

    private val _stopDetailState = MutableStateFlow(StopDetailState())
    val stopDetailState = _stopDetailState.asStateFlow()

    private val _createRouteFormState = MutableStateFlow(CreateRouteFormState())
    val createRouteFormState = _createRouteFormState.asStateFlow()

    private val _routePolyline = MutableStateFlow<List<Coordinates>>(emptyList())
    val routePolyline = _routePolyline.asStateFlow()

    private val _currentLocation = MutableStateFlow<LocationBias?>(null)
    private val locationBiasProvider: suspend () -> LocationBias? = {
        _currentLocation.value
    }

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
                        println("-----success------")
                        println("$suggestions")
                        placesState.update {
                            it.copy(
                                suggestions = suggestions,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { e ->
                        println("-----failure------")
                        println(e.stackTraceToString())
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

    // estados disponibles para el dropdown
    val stopStates: StateFlow<List<StopState>> = stopRepository
        .observeAllStates()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun onMarkStopState(stop: UserStop, newState: StopState) {
        viewModelScope.launch {
            stopRepository.updateState(stop.id, newState.id.toLong())
            _stopDetailState.update {
                it.copy(stop = stop.copy(state = newState.description))
            }
        }
    }

    fun onFinalizeRoute() {
        viewModelScope.launch {
            userRoute.value?.id?.let { routeId ->
                routeRepository.updateState(routeId, RouteStateEnum.FINISHED.id)
                clearRoute()
            }
        }
    }

    fun onLocationCaptured(latitude: Double, longitude: Double) {
        _currentLocation.update {
            LocationBias(
                circle = Circle(
                    center = LatLngDto(latitude, longitude),
                    radius = 25_000.0
                )
            )
        }
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

    private val _selectedRouteId = MutableStateFlow<String?>(null)
    val userRoute: StateFlow<UserRoute?> = _selectedRouteId
        .flatMapLatest { id ->
            if (id == null) {
                flowOf(null)
            } else {
                routeRepository.observeRoute(id)
            }
        }
        .onEach { route ->
            _routePolyline.update {
                if (route?.encodedPolyline != null)
                    PolylineDecoder.decode(route.encodedPolyline)
                else
                    emptyList()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    var routeSummary: MutableStateFlow<List<NotifierRouteSummary>> = MutableStateFlow(listOf())
        private set
    var routes: MutableStateFlow<List<UserRoute>> = MutableStateFlow(emptyList())
        private set

    /*val routePolyline: StateFlow<List<Coordinates>> = userRoute
        .map { route ->
            route?.encodedPolyline
                ?.takeIf { it.isNotBlank() }
                ?.let { PolylineDecoder.decode(it) }
                ?: emptyList()
        }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )*/
    //eliminar onEach de userRoute y el asignado manual de _updatePolyline en optimizeRoute

    fun selectRoute(id: String) {
        _selectedRouteId.value = id
    }

    fun clearRoute() {
        _selectedRouteId.value = null
    }


    fun onOpenCreateRoute(currentLatitude: Double?, currentLongitude: Double?) {
        _createRouteFormState.update { CreateRouteFormState() }
        if (currentLatitude != null && currentLongitude != null) {
            _createRouteFormState.update {
                it.copy(
                    originLatitude = currentLatitude,
                    originLongitude = currentLongitude,
                    isResolvingOrigin = true
                )
            }
            viewModelScope.launch {
                routeRepository
                    .getAddressFromCoordinates(currentLatitude, currentLongitude)
                    .fold(
                        onSuccess = { address ->
                            _createRouteFormState.update {
                                it.copy(
                                    originDir = address,
                                    isResolvingOrigin = false
                                )
                            }
                        },
                        onFailure = {
                            _createRouteFormState.update {
                                it.copy(isResolvingOrigin = false)
                            }
                        }
                    )
            }
        }
    }

    fun onCreateRouteNameChange(name: String) =
        _createRouteFormState.update { it.copy(name = name) }

    fun onCreateRouteDateChange(date: Instant) =
        _createRouteFormState.update { it.copy(selectedDate = date) }

    fun onCreateRouteOriginSelected(
        address: String,
        placeId: String?,
        latitude: Double,
        longitude: Double
    ) = _createRouteFormState.update {
        it.copy(
            originDir = address,
            originPlaceId = placeId,
            originLatitude = latitude,
            originLongitude = longitude,
        )
    }

    /*fun createRoute(userRoute: UserRoute) {
        println("--onCreateRoute--1")
        viewModelScope.launch(Dispatchers.Default) {
            val userId = sessionLocalDataSource.getSession()!!.id
            val id = routeRepository.insertRoute(
                userRoute = userRoute,
                userId = userId
            )
            selectRoute(id)
        }
    }*/

    fun createRoute() {
        val form = _createRouteFormState.value
        if (!form.isValid) return
        viewModelScope.launch(Dispatchers.Default) {
            val userId = sessionLocalDataSource.getSession()?.id ?: 2
            val route = UserRoute(
                id = generateId(),
                name = form.name,
                state = RouteStateEnum.ACTIVE.description,
                createdAt = form.selectedDate,
                originDir = form.originDir,
                originPlaceId = form.originPlaceId,
                originLatitude = form.originLatitude,
                originLongitude = form.originLongitude,
                destinationLatitude = 0.0,
                destinationLongitude = 0.0,
                destinationDir = "",
            )
            val id = routeRepository.insertRoute(userRoute = route, userId = userId)
            selectRoute(id)
        }
    }

    // ─── Ruta óptima ──────────────────────────────────────────────

    fun optimizeRoute() {
        val route = userRoute.value ?: return
        val stops = route.userStops
        if (stops.isEmpty()) return

        viewModelScope.launch {
            _createRouteFormState.update { it.copy(isOptimizing = true) }
            routeRepository.computeOptimalRoute(
                originLatitude = route.originLatitude,
                originLongitude = route.originLongitude,
                stops = stops
            ).fold(
                onSuccess = { result ->
                    println("-----optimize success------")
                    // reordenar paradas según el índice óptimo
                    val reordered =
                        result.orderedStopIndices.mapIndexed { newOrder, originalIndex ->
                            stops[originalIndex].copy(order = newOrder + 1)
                        }

                    println("-----reordered: $reordered------")

                    stopRepository.reorderStopsLocally(
                        route.id,
                        reordered
                    )

                    // guardar polyline localmente
                    routeRepository.updatePolyline(route.id, result.encodedPolyline)
                    // decodificar y mostrar polyline
                    _routePolyline.update {
                        PolylineDecoder.decode(result.encodedPolyline).map {
                            Coordinates(it.latitude, it.longitude)
                        }
                    }
                    _createRouteFormState.update { it.copy(isOptimizing = false) }
                },
                onFailure = { e ->
                    _createRouteFormState.update {
                        it.copy(isOptimizing = false, error = e.message)
                    }
                }
            )
        }
    }

    fun getAllRoutes() {
        viewModelScope.launch(Dispatchers.Default) {
            val pepe = routeRepository.selectAll()
            routes.value = pepe
        }
    }

    fun getAll() {
        viewModelScope.launch(Dispatchers.Default) {
            routeRepository.observeRouteSummaries().collectLatest {
                routeSummary.value = it
            }
        }
    }

    fun getById(routeId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = routeRepository.observeRoute(routeId)
            /*if (result.isSuccess) {
                route.value = result.getOrDefault(null)
            }*/
        }
    }

    fun updateRoute(userRoute: UserRoute) {
        /*viewModelScope.launch(Dispatchers.Default) {
            routeRepository.updateRoute(route)
        }*/
    }

    fun updateRouteName(id: String, name: String) {
        viewModelScope.launch(Dispatchers.Default) {
            routeRepository.updateName(id, name)
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

    fun deleteRoute(routeId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            routeRepository.softDelete(routeId)
            clearRoute()
            //route.value = null
        }
    }

    // ─── Crear parada ─────────────────────────────────────────────

    fun onSearchStopClick() {
        println("onSearchStopClick")
        _stopFormState.update { it.copy(isSearchingDirection = true) }
        println("-----${_stopFormState.value}")
    }

    fun onStopDirectionSelected(
        direction: String,
        placeId: String?,
        latitude: Double,
        longitude: Double
    ) {
        _stopFormState.update {
            it.copy(
                direction = direction,
                directionPlaceId = placeId,
                latitude = latitude,
                longitude = longitude,
                isSearchingDirection = false,
                isFillingForm = true
            )
        }
    }

    fun onStopRecipientChange(value: String) =
        _stopFormState.update { it.copy(recipient = value) }

    fun onStopNoticeChange(value: StopNoticeEnum) =
        _stopFormState.update { it.copy(notice = value) }

    fun onStopNoteChange(value: String) =
        _stopFormState.update { it.copy(note = value) }

    fun onDismissStopForm() =
        _stopFormState.update { StopFormState() }

    fun onCreateStop() {
        println("--onCreateStop--1")
        val form = _stopFormState.value
        val routeId = userRoute.value?.id ?: return
        viewModelScope.launch {
            _stopFormState.update { it.copy(isLoading = true) }
            try {
                val newStop = UserStop(
                    id = generateId(),
                    routeId = routeId,
                    notice = form.notice.description,
                    state = StopStateEnum.PENDIENTE.description,
                    recipient = form.recipient,
                    direction = form.direction,
                    directionPlaceId = form.directionPlaceId,
                    latitude = form.latitude,
                    longitude = form.longitude,
                    order = 0,
                    note = form.note.ifBlank { null }
                )
                println("--id: ${newStop.id}")
                stopRepository.insertStop(newStop, routeId)
                _stopFormState.update { StopFormState() }
            } catch (e: Exception) {
                _stopFormState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

// ─── Detalle / edición de parada ──────────────────────────────

    fun onStopClick(stop: UserStop) {
        _stopDetailState.update {
            StopDetailState(stop = stop, noteInput = stop.note ?: "")
        }
    }

    fun onDismissStopDetail() {
        _stopFormState.update { it.copy(isSearchingDirection = false) }
        _stopDetailState.update { StopDetailState() }
    }

    fun onStopNoteInputChange(value: String) =
        _stopDetailState.update { it.copy(noteInput = value) }

    fun onSaveStopNote() {
        val state = _stopDetailState.value
        val stop = state.stop ?: return
        viewModelScope.launch {
            val updated = stop.copy(note = state.noteInput.ifBlank { null })
            stopRepository.updateStop(updated)
            _stopDetailState.update { it.copy(stop = updated) }
        }
    }

    fun onMarkStopState(stop: UserStop, newState: StopStateEnum) {
        viewModelScope.launch {
            val stateId = newState.id.toLong()
            stopRepository.updateState(stop.id, stateId)
            _stopDetailState.update { it.copy(stop = stop.copy(state = newState.description)) }
            // verifica si todas las paradas están completadas
            checkRouteCompletion(stop.id, newState.description)
        }
    }

    fun onDeleteStop(stopId: String) {
        viewModelScope.launch {
            stopRepository.softDelete(stopId)
            _stopDetailState.update { StopDetailState() }
        }
    }

    private fun checkRouteCompletion(id: String, description: String) {
        val route = userRoute.value ?: return

        // aplica el cambio localmente sin esperar al Flow
        val updatedStops = route.userStops.map { stop ->
            if (stop.id == id) stop.copy(state = description) else stop
        }

        if (updatedStops.isEmpty()) return

        val allCompleted = updatedStops.all {
            it.state == StopStateEnum.EXITOSA.description ||
                    it.state == StopStateEnum.FALLIDA.description
        }
        if (allCompleted) {
            viewModelScope.launch {
                routeRepository.updateState(
                    route.id,
                    RouteStateEnum.FINISHED.id
                )
            }
        }
    }

    fun onClearPlacesState() {
        placesState.update { AddressSearchState() }
    }
}
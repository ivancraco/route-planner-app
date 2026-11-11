package com.routeplanner.app.features.home.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.routeplanner.app.core.utils.SyncManager
import com.routeplanner.app.features.auth.presentation.AuthScreen
import com.routeplanner.app.features.auth.presentation.navigation.AuthRoute
import com.routeplanner.app.features.home.presentation.AddressSearchField2
import com.routeplanner.app.features.home.presentation.NotifierHomeRoute
import com.routeplanner.app.features.home.presentation.SupervisorHomeRoute
import com.routeplanner.app.features.home.presentation.CreateRouteScreen
import com.routeplanner.app.features.home.presentation.MainScreen
import com.routeplanner.app.features.home.presentation.NotifierViewModel
import com.routeplanner.app.features.home.presentation.StopDetailPanel
import com.routeplanner.app.features.home.presentation.StopFormDialog
import com.routeplanner.app.features.home.presentation.SupervisorDetailRoute
import com.routeplanner.app.features.home.presentation.SupervisorHomeScreen
import com.routeplanner.app.features.home.presentation.SupervisorRouteDetailScreen
import com.routeplanner.app.features.home.presentation.SupervisorViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object Home

@Serializable
data class NewRoute(
    val latitude: Double?,
    val longitude: Double?
)

@Serializable
data class FindDirections(val addressType: String)

@Serializable
data class SearchDirectionRoute(
    val latitude: Double = -31.39684965666937,
    val longitude: Double = -58.017147576961506
)

@Serializable
data class SearchDirectionStop(
    val latitude: Double = -31.39684965666937,
    val longitude: Double = -58.017147576961506
)

@Serializable
object FormStop

@Serializable
object StopDetail

@Composable
fun NavigationWrapper(
    viewModel: NotifierViewModel = koinViewModel(),
    //supervisorViewModel: SupervisorViewModel = koinViewModel(),
    syncManager: SyncManager = koinInject()
) {
    val navController = rememberNavController()
    val routeState = viewModel.userRoute.collectAsState()
    val allRoutes = viewModel.routeSummary.collectAsStateWithLifecycle()
    val placesState = viewModel.placesState.collectAsState()
    val createRouteFormState by viewModel.createRouteFormState.collectAsStateWithLifecycle()
    val routePolyline by viewModel.routePolyline.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = NotifierHomeRoute) {
        composable<Home> {
            /*RoutePlannerScreen(
                route = routeState.value,
                allRoutes = allRoutes.value,
                onCreateRoute = { latitude, longitude ->
                    navController.navigate(NewRoute(latitude, longitude))
                },
                onChangeRoute = { routeId ->
                    viewModel.selectRoute(routeId)
                },
                onUpdateRouteName = { id, name ->
                    viewModel.updateRouteName(id, name)
                },
                onDeleteRoute = { routeId ->
                    viewModel.deleteRoute(routeId)
                }
            )*/
        }
        composable<NewRoute> {
            val coordinates by viewModel.coordinates.collectAsStateWithLifecycle()
            LaunchedEffect(coordinates) {
                if (coordinates != null && !createRouteFormState.isOriginResolved) {
                    viewModel.onOpenCreateRoute(
                        currentLatitude = coordinates!!.latitude,
                        currentLongitude = coordinates!!.longitude
                    )
                }
            }
            CreateRouteScreen(
                placesState = placesState.value,
                onDismiss = {
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                /*onRequestLocationPermission = {},
                onPickAddress = {},
                onFindDirections = { addressType ->
                    navController.navigate(FindDirections(addressType))
                },*/
                onCreateRoute = { ->
                    viewModel.createRoute()
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(suggestion, onResolved)
                },
                onValueChange = { value ->
                    viewModel.onQueryChanged(value)
                },
                onClear = {
                    viewModel.clear()
                },
                formState = createRouteFormState,
                onNameChange = { name ->
                    viewModel.onCreateRouteNameChange(name)
                },
                onDateChange = { date ->
                    viewModel.onCreateRouteDateChange(date)
                },
                onClearPlacesState = {
                    viewModel.onClearPlacesState()
                },
                onOriginSelected = { address, placeId, latitude, longitude ->
                    viewModel.onCreateRouteOriginSelected(address, placeId, latitude, longitude)
                },
                onNavigateToAddressSearchScreen = {
                    navController.navigate(
                        SearchDirectionRoute(
                            latitude = coordinates?.latitude ?: -31.39684965666937,
                            longitude = coordinates?.longitude ?: -58.017147576961506
                        )
                    )
                }
            )
        }
        composable<FindDirections> {
            val data = it.toRoute<FindDirections>()
            /*AddressSearchField(
                state = placesState.value,
                onAddressSelected = {},
                onValueChange = {
                    viewModel.onQueryChanged(it)
                },
                clear = {
                    viewModel.clear()
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(data.addressType, suggestion, onResolved)
                },
                label = "Buscar dirección"
            )*/
        }

        composable<SearchDirectionRoute> {
            val data = it.toRoute<SearchDirectionRoute>()
            var latitude by remember { mutableStateOf(data.latitude) }
            var longitude by remember { mutableStateOf(data.longitude) }

            AddressSearchField2(
                state = placesState.value,
                latitude = latitude,
                longitude = longitude,
                onConfirmAction = {
                    viewModel.clear()
                    navController.popBackStack()
                },
                onBack = {
                    viewModel.clear()
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                onValueChange = { value ->
                    viewModel.onQueryChanged(value)
                },
                onAddressSelected = { suggestion, selected ->
                    latitude = selected.latitude
                    longitude = selected.longitude
                    //println("datos: $latitude $longitude")
                    viewModel.onCreateRouteOriginSelected(
                        suggestion.primaryText,
                        suggestion.placeId,
                        selected.latitude,
                        selected.longitude
                    )
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(suggestion, onResolved)
                },
                onReverseGeocoding = { latitude, longitude ->
                    viewModel.onOpenCreateRoute(latitude, longitude)
                },
                onClear = {
                    viewModel.clear()
                }
            )
        }

        composable<SearchDirectionStop> {
            val data = it.toRoute<SearchDirectionStop>()
            var latitude by remember { mutableStateOf(data.latitude) }
            var longitude by remember { mutableStateOf(data.longitude) }

            AddressSearchField2(
                state = placesState.value,
                latitude = latitude,
                longitude = longitude,
                onConfirmAction = { // navegar al formulario
                    navController.navigate(FormStop)
                    //viewModel.clear()
                    //viewModel.onClearPlacesState()
                    //viewModel.onDismissStopDetail()
                    //navController.popBackStack()
                },
                onBack = {
                    viewModel.clear()
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                onValueChange = { value ->
                    viewModel.onQueryChanged(value)
                },
                onAddressSelected = { suggestion, address ->
                    latitude = address.latitude
                    longitude = address.longitude
                    viewModel.onStopDirectionSelected(
                        address.formattedAddress,
                        suggestion.placeId,
                        address.latitude,
                        address.longitude
                    )
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(suggestion, onResolved)
                },
                onReverseGeocoding = { latitude, longitude ->
                    viewModel.onStopReverseGeocoding(latitude, longitude)
                },
                onClear = {
                    viewModel.clear()
                }
            )
        }

        composable<FormStop> {
            val notices by viewModel.notices.collectAsStateWithLifecycle()
            StopFormDialog(
                state = viewModel.stopFormState.collectAsState().value,
                notices = notices,
                onRecipientChange = { value ->
                    viewModel.onStopRecipientChange(value)
                },
                onDirectionDetailChange = { value ->
                    viewModel.onDirectionDetailChange(value)
                },
                onNoticeChange = { value ->
                    viewModel.onStopNoticeChange(value)
                },
                onNoteChange = { value ->
                    viewModel.onStopNoteChange(value)
                },
                onConfirm = {
                    viewModel.onCreateStop()
                    navController.popBackStack(
                        route = NotifierHomeRoute,
                        inclusive = false
                    )
                    viewModel.onDismissStopForm()
                },
                onConfirmAndOther = {
                    viewModel.onCreateStop()
                    navController.popBackStack()
                    //viewModel.onDismissStopForm()
                },
                onDismiss = {
                    viewModel.onDismissStopForm()
                },
                onBack = {
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                }
            )
        }

        composable<StopDetail> {
            val stopDetailState = viewModel.stopDetailState.collectAsState().value
            val stopStates by viewModel.stopStates.collectAsStateWithLifecycle()
            val notices by viewModel.notices.collectAsStateWithLifecycle()
            StopDetailPanel(
                state = stopDetailState,
                stopStates = stopStates,
                notices = notices,
                onStateChange = { state ->
                    stopDetailState.stop?.let { userStop ->
                        viewModel.onMarkStopState(userStop, state)
                    }
                },
                onNoteChange = { value ->
                    viewModel.onStopNoteInputChange(value)
                },
                onSaveNote = {
                    viewModel.onSaveStopNote()
                },
                onMarkExitosa = { },
                onMarkFallida = { },
                onDelete = {
                    stopDetailState.stop?.id?.let {
                        viewModel.onDeleteStop(stopId = it)
                    }
                    navController.popBackStack()
                },
                onDismiss = {
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                }
            )
        }

        composable<AuthRoute> {
            AuthScreen(
                onNavigateAfterLogin = { isSupervisor ->
                    val destination = if (isSupervisor) SupervisorHomeRoute else NotifierHomeRoute
                    navController.navigate(destination) {
                        popUpTo<AuthRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<SupervisorHomeRoute> {
            val supervisorViewModel: SupervisorViewModel = koinViewModel()
            val supervisorRouteState by supervisorViewModel.supervisorRouteState.collectAsStateWithLifecycle()
            val state by supervisorViewModel.state.collectAsStateWithLifecycle()
            SupervisorHomeScreen(
                state = state,
                supervisorRouteState = supervisorRouteState,
                onLoadMore = {
                    supervisorViewModel.loadNextPage()
                },
                onSearch = { query ->
                    if (query.isEmpty()) {
                        supervisorViewModel.setUsername(null)
                        supervisorViewModel.refresh()
                        //supervisorViewModel.getAllRoutesFromApi()
                    } else {
                        supervisorViewModel.setUsername(query)
                        supervisorViewModel.refresh()
                        //supervisorViewModel.getRouteByUsername(query)
                    }
                },
                onRetry = { supervisorViewModel.loadNextPage() },
                onRefresh = {
                    supervisorViewModel.refresh()
                },
                onRouteSelected = { routeSelected ->
                    supervisorViewModel.setRouteSummary(routeSelected)
                    navController.navigate(SupervisorDetailRoute)
                }
            )
        }

        composable<SupervisorDetailRoute> {
            val parentEntry = remember(navController.currentBackStackEntry) {
                navController.getBackStackEntry(SupervisorHomeRoute)
            }
            val supervisorViewModel: SupervisorViewModel =
                koinViewModel(viewModelStoreOwner = parentEntry)
            supervisorViewModel.routeSummarySelected?.let {
                SupervisorRouteDetailScreen(
                    route = it,
                    onBack = {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }

        composable<NotifierHomeRoute> {
            syncManager.start()
            viewModel.getAllRoutesSummary()
            val stopStates by viewModel.stopStates.collectAsStateWithLifecycle()
            val notices by viewModel.notices.collectAsStateWithLifecycle()
            MainScreen(
                userRoute = routeState.value,
                allRoutes = allRoutes.value,
                routePolyline = routePolyline,
                createRouteFormState = createRouteFormState,
                onCreateRoute = { latitude, longitude ->
                    navController.navigate(NewRoute(latitude, longitude))
                },
                onChangeRoute = { routeId ->
                    viewModel.selectRoute(routeId)
                },
                onFinalizeRoute = { viewModel.onFinalizeRoute() },
                onUpdateRouteName = { id, name ->
                    viewModel.updateRouteName(id, name)
                },
                onDeleteRoute = { routeId ->
                    viewModel.deleteRoute(routeId)
                },
                onLocationCaptured = { latitude, longitude ->
                    viewModel.onLocationCaptured(latitude, longitude)
                },
                stopStates = stopStates,
                onStopClick = { stop ->
                    //viewModel.onStopClick(stop)
                },
                onStopDetailScreen = { stop ->
                    viewModel.onStopClick(stop)
                    navController.navigate(StopDetail)
                },
                onSearchStop = {
                    viewModel.onSearchStopClick()
                    navController.navigate(SearchDirectionStop())
                },
                onOptimizeRoute = {
                    viewModel.optimizeRoute()
                },
                isOptimizing = createRouteFormState.isOptimizing,
            )
            /*RoutePlannerScreen(
                userRoute = routeState.value,
                allRoutes = allRoutes.value,
                routePolyline = routePolyline,
                stopStates = stopStates,
                notices = notices,
                onCreateRoute = { latitude, longitude ->
                    navController.navigate(NewRoute(latitude, longitude))
                },
                createRouteFormState = createRouteFormState,
                onOptimizeRoute = {
                    viewModel.optimizeRoute()
                },
                onChangeRoute = { routeId ->
                    viewModel.selectRoute(routeId)
                },
                onUpdateRouteName = { id, name ->
                    viewModel.updateRouteName(id, name)
                },
                onDeleteRoute = { routeId ->
                    viewModel.deleteRoute(routeId)
                },
                stopFormState = viewModel.stopFormState.collectAsState().value,
                stopDetailState = viewModel.stopDetailState.collectAsState().value,
                placesState = placesState.value,
                onSearchStop = {
                    viewModel.onSearchStopClick()
                },
                onStopDirectionSelected = { direction, placeId, latitude, longitude ->
                    viewModel.onStopDirectionSelected(direction, placeId, latitude, longitude)
                },
                onQueryChanged = { value ->
                    viewModel.onQueryChanged(value)
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(suggestion, onResolved)
                },
                onClearQuery = {
                    viewModel.clear()
                },
                onDismissStopSearch = {
                    viewModel.onDismissStopDetail()
                },
                onStopRecipientChange = { value ->
                    viewModel.onStopRecipientChange(value)
                },
                onStopNoticeChange = { value ->
                    viewModel.onStopNoticeChange(value)
                },
                onStopNoteChange = { value ->
                    viewModel.onStopNoteChange(value)
                },
                onDismissStopForm = {
                    viewModel.onDismissStopForm()
                },
                onCreateStop = {
                    viewModel.onCreateStop()
                },
                onStopClick = { stop ->
                    viewModel.onStopClick(stop)
                },
                onSearchStopClick = {
                    viewModel.onSearchStopClick()
                    navController.navigate(SearchDirectionStop())
                },
                onDismissStopDetail = {
                    viewModel.onDismissStopDetail()
                },
                onStopNoteInputChange = { value ->
                    viewModel.onStopNoteInputChange(value)
                },
                onStopReverseGeocoding = { latitude, longitude ->
                    viewModel.onStopReverseGeocoding(latitude, longitude)
                },
                onSaveStopNote = {
                    viewModel.onSaveStopNote()
                },
                onMarkExitosa = { stop ->
                    //viewModel.onMarkStopState(stop, StopStateEnum.EXITOSA)
                },
                onMarkFallida = { stop ->
                    //viewModel.onMarkStopState(stop, StopStateEnum.FALLIDA)
                },
                onDeleteStop = { stopId ->
                    viewModel.onDeleteStop(stopId)
                },
                onLocationCaptured = { latitude, longitude ->
                    viewModel.onLocationCaptured(latitude, longitude)
                },
                onFinalizeRoute = { viewModel.onFinalizeRoute() },
                onMarkStopState = { stop, state -> viewModel.onMarkStopState(stop, state) },
                onClearPlacesState = {
                    viewModel.onClearPlacesState()
                }
            )*/
        }
    }
}

enum class AddressType(val type: String) {
    ROUTE_ORIGIN("route_origin"),
    ROUTE_DESTINATION("route_destination"),
    STOP("stop")
}
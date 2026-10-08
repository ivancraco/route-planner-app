package com.routeplanner.app.features.home.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.auth.presentation.AuthScreen
import com.routeplanner.app.features.auth.presentation.navigation.AuthRoute
import com.routeplanner.app.features.auth.presentation.navigation.HomeNotifierRoute
import com.routeplanner.app.features.auth.presentation.navigation.HomeSupervisorRoute
import com.routeplanner.app.features.home.domain.model.StopStateEnum
import com.routeplanner.app.features.home.presentation.CreateRouteScreen
import com.routeplanner.app.features.home.presentation.NotifierViewModel
import com.routeplanner.app.features.home.presentation.RoutePlannerScreen
import kotlinx.serialization.Serializable
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

@Composable
fun NavigationWrapper(
    viewModel: NotifierViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val routeState = viewModel.userRoute.collectAsState()
    val allRoutes = viewModel.routeSummary.collectAsStateWithLifecycle()
    val placesState = viewModel.placesState.collectAsState()
    val createRouteFormState by viewModel.createRouteFormState.collectAsStateWithLifecycle()
    val routePolyline by viewModel.routePolyline.collectAsStateWithLifecycle()

    viewModel.getAll()
    NavHost(navController = navController, startDestination = HomeNotifierRoute) {
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
            val data = it.toRoute<NewRoute>()
            LaunchedEffect(Unit) {
                viewModel.onOpenCreateRoute(data.latitude, data.longitude)
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
        composable<AuthRoute> {
            AuthScreen(
                onNavigateAfterLogin = { isSupervisor ->
                    val destination = if (isSupervisor) HomeSupervisorRoute else HomeNotifierRoute
                    navController.navigate(destination) {
                        popUpTo<AuthRoute> { inclusive = true }
                    }
                }
            )
        }

        composable<HomeSupervisorRoute> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(RoutePlannerTheme.colors.primary)
                    .statusBarsPadding()
                    .padding(
                        horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                        vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                    )
            ) {
                Text(
                    text = "Supervisor",
                    style = RoutePlannerTheme.typography.titleLarge,
                    color = RoutePlannerTheme.colors.onPrimary
                )
            }
        }

        composable<HomeNotifierRoute> {
            val stopStates by viewModel.stopStates.collectAsStateWithLifecycle()
            RoutePlannerScreen(
                userRoute = routeState.value,
                allRoutes = allRoutes.value,
                routePolyline = routePolyline,
                stopStates = stopStates,
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
                },
                onDismissStopDetail = {
                    viewModel.onDismissStopDetail()
                },
                onStopNoteInputChange = { value ->
                    viewModel.onStopNoteInputChange(value)
                },
                onSaveStopNote = {
                    viewModel.onSaveStopNote()
                },
                onMarkExitosa = { stop ->
                    viewModel.onMarkStopState(stop, StopStateEnum.EXITOSA)
                },
                onMarkFallida = { stop ->
                    viewModel.onMarkStopState(stop, StopStateEnum.FALLIDA)
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
            )
        }
    }
}

enum class AddressType(val type: String) {
    ROUTE_ORIGIN("route_origin"),
    ROUTE_DESTINATION("route_destination"),
    STOP("stop")
}
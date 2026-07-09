package com.routeplanner.app.features.notifier.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
//import com.routeplanner.app.features.notifier.presentation.AddressSearchField
import com.routeplanner.app.features.notifier.presentation.CreateRouteScreen
import com.routeplanner.app.features.notifier.presentation.NotifierViewModel
import com.routeplanner.app.features.notifier.presentation.RoutePlannerScreen
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
    val routeState = viewModel.route.collectAsState()
    val allRoutes = viewModel.routeSummary.collectAsState()
    val placesState = viewModel.placesState.collectAsState()
    viewModel.getAll()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            RoutePlannerScreen(
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
            )
        }
        composable<NewRoute> {
            val data = it.toRoute<NewRoute>()
            CreateRouteScreen(
                placesState = placesState.value,
                onDismiss = {
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                onRequestLocationPermission = {},
                onPickAddress = {},
                onFindDirections = { addressType ->
                    navController.navigate(FindDirections(addressType))
                },
                onCreateRoute = { notifierRoute ->
                    viewModel.createRoute(notifierRoute)
                },
                onSuggestionSelected = { suggestion, onResolved ->
                    viewModel.onSuggestionSelected(suggestion, onResolved)
                },
                onValueChange = { value ->
                    viewModel.onQueryChanged(value)
                },
                onClear = {
                    viewModel.clear()
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
    }
}

enum class AddressType(val type: String) {
    ROUTE_ORIGIN("route_origin"),
    ROUTE_DESTINATION("route_destination"),
    STOP("stop")
}
package com.routeplanner.app.features.notifier.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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

@Composable
fun NavigationWrapper(
    viewModel: NotifierViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val routeState = viewModel.state.collectAsState()
    val allRoutes = viewModel.summary.collectAsState()
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
                    viewModel.getById(routeId)
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
                originLatitude = data.latitude,
                originLongitude = data.longitude,
                destinationLatitude = data.latitude,
                destinationLongitude = data.longitude,
                onDismiss = {
                    if (navController.previousBackStackEntry != null)
                        navController.popBackStack()
                },
                onRequestLocationPermission = {},
                onPickAddress = {},
                onCreateRoute = { notifierRoute ->
                    viewModel.createRoute(notifierRoute)
                }
            )
        }
    }
}
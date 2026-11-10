package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.RouteStateEnum
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.location.LocationCaptureState
import com.routeplanner.app.features.home.location.LocationCoordinates
import com.routeplanner.app.features.home.location.rememberLocationCaptureController
import com.swmansion.kmpmaps.core.Coordinates
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    userRoute: UserRoute?,
    allRoutes: List<NotifierRouteSummary>,
    routePolyline: List<Coordinates> = emptyList(),
    onCreateRoute: (Double?, Double?) -> Unit,
    onChangeRoute: (String) -> Unit,
    onFinalizeRoute: () -> Unit,
    onUpdateRouteName: (String, String) -> Unit,
    onDeleteRoute: (String) -> Unit,
    onLocationCaptured: (Double, Double) -> Unit,
    onStopClick: (UserStop) -> Unit,
    onToggle: () -> Unit
) {
    //val locationCaptureController = rememberLocationCaptureController(Permission.COARSE_LOCATION)
    var isLocationEnabled by remember { mutableStateOf(false) }
    var coordinates by remember { mutableStateOf<LocationCoordinates?>(null) }
    //var state by remember { mutableStateOf<LocationCaptureState>(LocationCaptureState.Idle) }
    val scope = rememberCoroutineScope()
    //val panelState = rememberLeftPanelState()
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteRoute by remember { mutableStateOf(false) }

    /*LaunchedEffect(Unit) {
        state = locationCaptureController.captureLocation()
        when (state) {
            is LocationCaptureState.Success -> {
                coordinates = (state as LocationCaptureState.Success).coordinates
                isLocationEnabled = true
                onLocationCaptured(coordinates!!.latitude, coordinates!!.longitude)
            }

            else -> isLocationEnabled = false
        }
    }*/

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RoutePlannerTheme.colors.background)
            //.navigationBarsPadding()
            //.statusBarsPadding()
    ) {
        /*LeftPanelOverlay(
            state = panelState,
            panelContent = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(RoutePlannerTheme.colors.background)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                            )
                            .background(RoutePlannerTheme.colors.background),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Mis rutas",
                            color = RoutePlannerTheme.colors.onPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )
                        Spacer(Modifier.height(RoutePlannerTheme.dimens.spaceMd))
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
                        ) {
                            items(allRoutes) { route ->
                                ListItem(
                                    colors = ListItemDefaults.colors(
                                        containerColor = RoutePlannerTheme.colors.primary
                                    ),
                                    onClick = {
                                        scope.launch {
                                            //onDismissStopDetail()
                                            panelState.close()
                                        }
                                        onChangeRoute(route.id)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = RoutePlannerTheme.dimens.spaceMd),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = route.name,
                                            color = RoutePlannerTheme.colors.onPrimary
                                        )
                                        Text(
                                            text = route.createdAt.toArgentinaDateString(),
                                            style = RoutePlannerTheme.typography.labelSmall,
                                            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                onCreateRoute(
                                    coordinates?.latitude,
                                    coordinates?.longitude,
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary,
                            ),
                        ) {
                            Text(
                                text = "Nueva ruta",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            )
                        }
                    }
                }
            }
        )*/

        RouteMap(
            modifier = Modifier.fillMaxSize(),
            coordinates = coordinates,
            userRoute = userRoute,
            routePolyline = routePolyline,
            isMyLocationEnabled = isLocationEnabled,
            onMarkerClick = onStopClick,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = RoutePlannerTheme.dimens.spaceXs)
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {

            // Botón hamburguesa
            Surface(
                modifier = Modifier
                    .size(44.dp),
                shape = CircleShape,
                shadowElevation = 4.dp,
                color = RoutePlannerTheme.colors.primary,
            ) {
                IconButton(onClick = {
                    onToggle()
                    /*scope.launch {
                        //panelState.toggle()
                    }*/
                }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = RoutePlannerTheme.colors.onPrimary,
                    )
                }
            }

            if (userRoute != null) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = RoutePlannerTheme.colors.primary,
                            shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceMd)
                        )
                        .padding(
                            vertical = RoutePlannerTheme.dimens.spaceXxs,
                            horizontal = RoutePlannerTheme.dimens.spaceSm
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = userRoute.name,
                            style = RoutePlannerTheme.typography.titleMedium,
                            color = RoutePlannerTheme.colors.onPrimary
                        )
                        Text(
                            text = userRoute.createdAt.toArgentinaDateString(),
                            style = RoutePlannerTheme.typography.bodySmall,
                            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = userRoute.state,
                            style = RoutePlannerTheme.typography.bodySmall,
                            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                        )
                        IconButton(onClick = { showOptionsDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = null,
                                tint = RoutePlannerTheme.colors.onPrimary,
                            )
                        }
                    }
                }
            }
        }

        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.Center
        ) {
            if (userRoute != null && RouteStateEnum.fromName(userRoute.state).id == 1) {
                Button(
                    onClick = {
                        onFinalizeRoute()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Text(
                        text = "Finalizar ruta",
                        style = RoutePlannerTheme.typography.bodyMedium,
                    )
                }
            } else {
                Button(
                    onClick = {
                        onCreateRoute(
                            coordinates?.latitude,
                            coordinates?.longitude,
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Text(
                        text = "Crear nueva ruta",
                        style = RoutePlannerTheme.typography.bodyMedium
                    )
                }
            }
        }*/

        if (showOptionsDialog) {
            RouteOptions(
                onDismiss = { showOptionsDialog = false },
                onEditRoute = { showEditDialog = true },
                onDelete = { showConfirmDeleteRoute = true },
                onFinalize = { onFinalizeRoute() }
            )
        }

        if (showConfirmDeleteRoute) {
            ConfirmDeleteDialog(
                text = "¿Eliminar ruta?",
                onDismiss = { showConfirmDeleteRoute = false },
                onDelete = {
                    showConfirmDeleteRoute = false
                    showOptionsDialog = false
                    userRoute?.let {
                        onDeleteRoute(it.id)
                    }
                }
            )
        }

        if (showEditDialog) {
            EditRouteName(
                label = userRoute!!.name,
                //onValueChange = {},
                onConfirm = {
                    showEditDialog = false
                    onUpdateRouteName(userRoute.id, it)
                },
                onDismissRequest = { showEditDialog = false }
            )
        }
    }
}
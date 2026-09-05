package com.routeplanner.app.features.home.presentation

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.AddressSearchState
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum
import com.routeplanner.app.features.home.domain.model.StopStateEnum
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.location.LocationCaptureState
import com.routeplanner.app.features.home.location.LocationCoordinates
import com.routeplanner.app.features.home.location.rememberLocationCaptureController
import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.SelectedAddress
import com.swmansion.kmpmaps.core.Coordinates
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import kotlinx.coroutines.launch
import kotlin.math.abs

// ---------------------------------------------------------------------------
// Tamaños del bottom sheet
// ---------------------------------------------------------------------------

private val SHEET_EMPTY = 80.dp   // solo handle + botón "Crear nueva ruta"
private val SHEET_PARTIAL = 400.dp
private const val SHEET_EXPANDED_FRACTION = 0.85f

// ---------------------------------------------------------------------------
// Pantalla principal
// ---------------------------------------------------------------------------

@Composable
fun RoutePlannerScreen(
    routeName: String = "Ruta 001",
    userRoute: UserRoute?,
    allRoutes: List<NotifierRouteSummary>,
    routePolyline: List<Coordinates> = emptyList(),
    isMyLocationEnabled: Boolean = false,
    onMenuClick: () -> Unit = {},
    onSearchStop: () -> Unit = {},
    onCreateRoute: (Double?, Double?) -> Unit,
    onChangeRoute: (String) -> Unit,
    onUpdateRouteName: (String, String) -> Unit,
    onDeleteRoute: (String) -> Unit,
    stopFormState: StopFormState,
    stopDetailState: StopDetailState,
    placesState: AddressSearchState,
    onSearchStopClick: () -> Unit,
    onStopDirectionSelected: (String, String?, Double, Double) -> Unit,
    onQueryChanged: (String) -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
    onClearQuery: () -> Unit,
    onDismissStopSearch: () -> Unit,
    onStopRecipientChange: (String) -> Unit,
    onStopNoticeChange: (StopNoticeEnum) -> Unit,
    onStopNoteChange: (String) -> Unit,
    onDismissStopForm: () -> Unit,
    onCreateStop: () -> Unit,
    onStopClick: (UserStop) -> Unit,
    onDismissStopDetail: () -> Unit,
    onStopNoteInputChange: (String) -> Unit,
    onSaveStopNote: () -> Unit,
    onMarkExitosa: (UserStop) -> Unit,
    onMarkFallida: (UserStop) -> Unit,
    onDeleteStop: (String) -> Unit,
    createRouteFormState: CreateRouteFormState,
    onOptimizeRoute: () -> Unit,
    onLocationCaptured: (Double, Double) -> Unit
) {
    val locationCaptureController = rememberLocationCaptureController(Permission.COARSE_LOCATION)
    val scope = rememberCoroutineScope()
    var isLocationEnabled by remember { mutableStateOf(false) }
    var coordinates by remember { mutableStateOf<LocationCoordinates?>(null) }
    var state by remember { mutableStateOf<LocationCaptureState>(LocationCaptureState.Idle) }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showConfirmDeleteRoute by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        state = locationCaptureController.captureLocation()
        when (state) {
            is LocationCaptureState.Success -> {
                coordinates = (state as LocationCaptureState.Success).coordinates
                isLocationEnabled = true
                onLocationCaptured(coordinates!!.latitude, coordinates!!.longitude)
            }

            else -> isLocationEnabled = false
        }
    }

    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var containerHeightPx by remember { mutableFloatStateOf(0f) }

    // ── Los tres tamaños posibles en px ────────────────────────────────
    val emptyPx by remember(density) {
        derivedStateOf { with(density) { SHEET_EMPTY.toPx() } }
    }
    val partialPx by remember(density) {
        derivedStateOf { with(density) { SHEET_PARTIAL.toPx() } }
    }
    val expandedPx by remember(containerHeightPx) {
        derivedStateOf { containerHeightPx * SHEET_EXPANDED_FRACTION }
    }

    var sheetHeightPx by remember { mutableFloatStateOf(0f) }

    // Snap points disponibles según si hay ruta o no
    val availableSnaps by remember(userRoute, emptyPx, partialPx, expandedPx) {
        derivedStateOf {
            if (userRoute != null) listOf(emptyPx, partialPx, expandedPx) else emptyList()
        }
    }

    // Inicializa o reajusta sheetHeightPx cuando cambia route (null <-> no-null)
    LaunchedEffect(userRoute, emptyPx, partialPx) {
        sheetHeightPx = if (userRoute == null) {
            if (emptyPx > 0f) emptyPx else sheetHeightPx
        } else {
            if (partialPx > 0f) partialPx else sheetHeightPx
        }
    }

    suspend fun snapTo(target: Float) = animate(
        initialValue = sheetHeightPx,
        targetValue = target,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f),
    ) { value, _ -> sheetHeightPx = value }

    fun nearestSnap(height: Float): Float =
        availableSnaps.minByOrNull { abs(height - it) } ?: height

    // Límites de arrastre: si no hay ruta, el sheet queda fijo en EMPTY
    // (no se puede arrastrar entre PARTIAL/EXPANDED porque no existen).
    val dragMin = availableSnaps.minOrNull() ?: emptyPx
    val dragMax = availableSnaps.maxOrNull() ?: emptyPx

    val panelState = rememberLeftPanelState()


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(RoutePlannerTheme.colors.background)
            .navigationBarsPadding()
    ) {
        containerHeightPx = with(density) { maxHeight.toPx() }

        LeftPanelOverlay(
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
                            .background(RoutePlannerTheme.colors.background)
                    ) {
                        Text(
                            text = "Mis rutas",
                            color = RoutePlannerTheme.colors.onPrimary,
                        )
                        Spacer(Modifier.height(RoutePlannerTheme.dimens.spaceMd))
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
                        ) {
                            items(allRoutes) { route ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(RoutePlannerTheme.dimens.spaceMd)
                                        .clickable {
                                            scope.launch {
                                                panelState.close()
                                            }
                                            onChangeRoute(route.id)
                                        },
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = route.name,
                                        color = RoutePlannerTheme.colors.onPrimary
                                    )
                                    Text(
                                        text  = route.createdAt.toArgentinaDateString(),
                                        style = RoutePlannerTheme.typography.labelSmall,
                                        color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        // ── Mapa — ocupa toda la pantalla, detrás de todo ─────────────────
        RouteMap(
            modifier = Modifier.fillMaxSize(),
            coordinates = coordinates,
            userRoute = userRoute,
            routePolyline = routePolyline,
            isMyLocationEnabled = isLocationEnabled,
            onMarkerClick = onStopClick,
        )

        // ── Botón hamburguesa ──────────────────────────────────────────
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 12.dp, start = 16.dp)
                .size(44.dp)
                .align(Alignment.TopStart),
            shape = CircleShape,
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            IconButton(onClick = {
                scope.launch { panelState.toggle() }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        // ── Bottom sheet flotante sobre el mapa ───────────────────────────
        val sheetHeightDp: Dp = with(density) { sheetHeightPx.toDp() }

        if (userRoute != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(sheetHeightDp),
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    shadowElevation = 10.dp,
                    color = RoutePlannerTheme.colors.background,
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {

                        // Handle de arrastre — mismo patrón que ya tenías
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .pointerInput(dragMin, dragMax) {
                                    detectVerticalDragGestures(
                                        onDragEnd = {
                                            val target = nearestSnap(sheetHeightPx)
                                            coroutineScope.launch { snapTo(target) }
                                        },
                                        onVerticalDrag = { _, delta ->
                                            sheetHeightPx = (sheetHeightPx - delta)
                                                .coerceIn(dragMin, dragMax)
                                        },
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(width = 40.dp, height = 4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    ),
                            )
                        }


                        RouteSheetContent(
                            userRoute = userRoute,
                            onSearchStop = onSearchStopClick,
                            isOptimizing = createRouteFormState.isOptimizing,
                            onOptimizeRoute = onOptimizeRoute,
                            onStopClick = onStopClick,
                            onCreateRoute = {
                                onCreateRoute(
                                    coordinates?.latitude,
                                    coordinates?.longitude,
                                )
                            },
                            onOptionsDialog = { showOptionsDialog = true },
                            onDeleteStop = { onDeleteStop(it) },
                            onMarkExitosa = { onMarkExitosa(it) },
                            onDismissStopDetail = { onDismissStopDetail() },
                            onMarkFallida = { onMarkFallida(it) },
                            onStopNoteInputChange = { onStopNoteInputChange(it) },
                            onSaveStopNote = { onSaveStopNote() },
                            stopDetailState = stopDetailState
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        color = RoutePlannerTheme.colors.background,
                        shape = RoundedCornerShape(
                            topStart = RoutePlannerTheme.dimens.radiusLg,
                            topEnd = RoutePlannerTheme.dimens.radiusLg
                        )
                    )
                    .navigationBarsPadding()
                    .padding(
                        horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                        vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                    ),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "No tenés una ruta activa",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = {
                        onCreateRoute(
                            coordinates?.latitude,
                            coordinates?.longitude,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                    ),
                ) {
                    Text(
                        text = "Crear nueva ruta",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }
        }
        if (showOptionsDialog) {
            RouteOptions(
                onDismiss = { showOptionsDialog = false },
                onEditRoute = { showEditDialog = true },
                onDelete = { showConfirmDeleteRoute = true }
            )
        }

        if (showConfirmDeleteRoute) {
            ConfirmDeleteRouteDialog(
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

        // Dialog búsqueda de dirección para parada
        if (stopFormState.isSearchingDirection) {
            println("buscando dirección")
            AddressSearchField(
                state = placesState,
                onAddressSelected = { suggestion, address ->
                    onStopDirectionSelected(
                        address.formattedAddress,
                        suggestion.placeId,
                        address.latitude,
                        address.longitude
                    )
                },
                onValueChange = onQueryChanged,
                clear = onClearQuery,
                onSuggestionSelected = onSuggestionSelected,
                onDismiss = onDismissStopSearch,
                label = "Buscar punto de entrega"
            )
        }

        // Dialog formulario de parada
        if (stopFormState.isFillingForm) {
            StopFormDialog(
                state = stopFormState,
                onRecipientChange = onStopRecipientChange,
                onNoticeChange = onStopNoticeChange,
                onNoteChange = onStopNoteChange,
                onConfirm = onCreateStop,
                onDismiss = onDismissStopForm
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Contenido del sheet
// ---------------------------------------------------------------------------

@Composable
private fun RouteSheetContent(
    userRoute: UserRoute?,
    stopDetailState: StopDetailState,
    isOptimizing: Boolean,
    onOptimizeRoute: () -> Unit,
    onDismissStopDetail: () -> Unit,
    onStopNoteInputChange: (String) -> Unit,
    onSaveStopNote: () -> Unit,
    onMarkExitosa: (UserStop) -> Unit,
    onMarkFallida: (UserStop) -> Unit,
    onDeleteStop: (String) -> Unit,
    onSearchStop: () -> Unit,
    onStopClick: (UserStop) -> Unit,
    onCreateRoute: () -> Unit,
    onOptionsDialog: () -> Unit,
) {

    // Si hay parada seleccionada → mostrar detalle
    if (stopDetailState.stop != null) {
        StopDetailPanel(
            state = stopDetailState,
            onNoteChange = onStopNoteInputChange,
            onSaveNote = onSaveStopNote,
            onMarkExitosa = { onMarkExitosa(stopDetailState.stop) },
            onMarkFallida = { onMarkFallida(stopDetailState.stop) },
            onDelete = { onDeleteStop(stopDetailState.stop.id) },
            onDismiss = onDismissStopDetail
        )
        return
    }

    // Vista normal de la ruta
    Column(
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal),
    ) {
        if (userRoute != null) {
            // Header de ruta
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs)) {
                    Text(
                        text = userRoute.name,
                        style = RoutePlannerTheme.typography.titleMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = userRoute.state,
                        style = RoutePlannerTheme.typography.bodySmall,
                        color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                    )
                    Text(
                        text  = userRoute.createdAt.toArgentinaDateString(),
                        style = RoutePlannerTheme.typography.bodySmall,
                        color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onOptionsDialog) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary,
                    )
                }
            }

            // Buscador de parada
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        println("------pepe----------")
                        onSearchStop()
                    },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = "Agregar punto de entrega",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Lista de paradas
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                item {
                    StopRow(
                        stop = UserStop(
                            id = "origin", routeId = userRoute.id,
                            direction = userRoute.originDir,
                            recipient = "", notice = "", state = "",
                            latitude = userRoute.originLatitude,
                            longitude = userRoute.originLongitude,
                            order = 0, note = null,
                            directionPlaceId = userRoute.originPlaceId
                        ),
                        isOriginOrDestination = true,
                        markerColor = Color.Blue,
                        onClick = {}
                    )
                }
                items(userRoute.userStops, key = { it.id }) { stop ->
                    StopRow(
                        stop = stop,
                        markerColor = MarkerColors.waypoint,
                        onClick = { onStopClick(stop) }
                    )
                    if (stop.id != userRoute.userStops.lastOrNull()?.id) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 22.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                        )
                    }
                }
                /*item {
                    StopRow(
                        stop = UserStop(
                            id = "destination", routeId = userRoute.id,
                            direction = userRoute.destinationDir,
                            recipient = "", notice = "", state = "",
                            latitude = userRoute.destinationLatitude,
                            longitude = userRoute.destinationLongitude,
                            order = 999, note = null,
                            directionPlaceId = userRoute.destinationPlaceId
                        ),
                        isOriginOrDestination = true,
                        markerColor = Color.Green,
                        onClick = {}
                    )
                }*/
            }

            // Optimizar — solo si hay al menos 2 paradas
            if ((userRoute.userStops.size) >= 2) {
                Button(
                    onClick = onOptimizeRoute,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutePlannerTheme.colors.secondary.copy(alpha = 0.15f),
                        contentColor = RoutePlannerTheme.colors.secondary
                    )
                ) {
                    if (isOptimizing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = RoutePlannerTheme.colors.secondary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Route,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Optimizar ruta")
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))


        Button(
            onClick = onCreateRoute,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Text(
                text = "Crear nueva ruta",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            )
        }

        Spacer(Modifier.height(12.dp))
    }
    /*Column(
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal),
    )
    {
        if (userRoute != null) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Column(verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs)) {
                    Text(
                        text = userRoute.name,
                        style = RoutePlannerTheme.typography.titleMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = userRoute.state,
                        style = RoutePlannerTheme.typography.bodySmall,
                        color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onOptionsDialog() },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary,
                    )
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSearchStop() },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = "Agregar punto de entrega",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
            ) {
                item {
                    StopRow(
                        direction = userRoute.originDir,
                        color = Color.Blue,
                        onClick = { onSearchStop() },
                    )
                    VerticalDivider(
                        thickness = 0.5.dp,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                }
                items(userRoute.userStops, key = { it.id }) { stop ->
                    StopRow(
                        direction = stop.direction,
                        color = MarkerColors.waypoint,
                        onClick = { onStopClick(stop) },
                    )
                    if (stop.id != userRoute.userStops.lastOrNull()?.id) {
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 22.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            thickness = 0.5.dp,
                        )
                    }
                }
                item {
                    StopRow(
                        direction = userRoute.destinationDir,
                        color = Color.Green,
                        onClick = { onSearchStop() },
                    )
                }
            }
        } else {
            // Estado vacío: empuja el botón hacia abajo del sheet colapsado
            /*Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "No tenés una ruta activa",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )*/
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = onCreateRoute,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
            ),
        ) {
            Text(
                text = "Crear nueva ruta",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        // navigationBarsPadding() ya está aplicado en el BoxWithConstraints
        // raíz — no se necesita Spacer manual para la nav bar acá.
    }*/
}

// ---------------------------------------------------------------------------
// Fila de parada
// ---------------------------------------------------------------------------

/*@Composable
private fun StopRow(
    direction: String,
    color: Color,
    label: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = direction,
            style = RoutePlannerTheme.typography.bodyMedium,
            color = RoutePlannerTheme.colors.onPrimary,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = label,
            style = RoutePlannerTheme.typography.labelMedium,
            color = RoutePlannerTheme.colors.onPrimary.copy(0.5f),
        )
    }
}*/

@Composable
private fun StopRow(
    stop: UserStop,
    markerColor: Color = MarkerColors.waypoint,
    isOriginOrDestination: Boolean = false,
    onClick: () -> Unit,
) {
    val stateIcon: Pair<ImageVector, Color>? = when {
        isOriginOrDestination -> null
        stop.state == StopStateEnum.EXITOSA.description ->
            Icons.Default.CheckCircle to Color(0xFF34A853)

        stop.state == StopStateEnum.FALLIDA.description ->
            Icons.Default.Cancel to MaterialTheme.colorScheme.error

        else ->
            Icons.Default.RadioButtonUnchecked to
                    RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.3f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
            .then(if (!isOriginOrDestination) Modifier.clickable { onClick() } else Modifier)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(markerColor),
        )
        Column(modifier = Modifier.weight(1f)) {
            val shortAddress = stop.direction.split(",")
                .firstOrNull()
                ?.trim() ?: stop.direction
            Text(
                text = shortAddress,
                style = RoutePlannerTheme.typography.bodyMedium,
                color = RoutePlannerTheme.colors.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!isOriginOrDestination && stop.recipient.isNotBlank()) {
                Text(
                    text = stop.recipient,
                    style = RoutePlannerTheme.typography.labelSmall,
                    color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        stateIcon?.let { (icon, tint) ->
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteOptions(
    onDismiss: () -> Unit,
    onEditRoute: () -> Unit,
    onDelete: () -> Unit,
) {
    ModalBottomSheet(
        containerColor = RoutePlannerTheme.colors.primaryContainer,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = RoutePlannerTheme.colors.primaryContainer,
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
                )
                .padding(
                    horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                    vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                )
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceLg),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(RoutePlannerTheme.dimens.spaceXl)
                    .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
                    .clickable {
                        onDismiss()
                        onEditRoute()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
                Text(
                    text = "Editar ruta",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = RoutePlannerTheme.colors.onPrimary,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(RoutePlannerTheme.dimens.spaceXl)
                    .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
                    .clickable {
                        onDismiss()
                        onDelete()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
                Text(
                    text = "Eliminar ruta",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = RoutePlannerTheme.colors.onPrimary,
                )
            }
        }
    }
}

@Composable
fun ConfirmDeleteRouteDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .padding(16.dp)
                    .background(
                        RoutePlannerTheme.colors.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
        ) {
            Text(
                text = "¿Eliminar ruta?",
                style = RoutePlannerTheme.typography.titleLarge,
                color = RoutePlannerTheme.colors.onPrimary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDismiss() },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Cancelar")
                }
                Button(
                    onClick = { onDelete() },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EditRouteName(
    title: String = "Editar nombre de ruta",
    label: String = "Nombre de la ruta",
    placeholder: String = "Ruta 001",
    limitText: Int = 25,
    isError: Boolean = false,
    errorMessage: String = "",
    //onValueChange: (String) -> Unit,
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(text = label)) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .padding(16.dp)
                    .background(
                        RoutePlannerTheme.colors.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
        ) {
            Text(
                text = title,
                style = RoutePlannerTheme.typography.titleLarge,
                color = RoutePlannerTheme.colors.onPrimary
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = text,
                    onValueChange = {
                        if (it.text.length <= limitText) {
                            text = it
                            //onValueChange(it.text)
                        }
                    },
                    placeholder = { Text(text = placeholder) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                text = text.copy(
                                    selection = TextRange(text.text.length)
                                )
                            }
                        },
                    singleLine = true,
                    minLines = 1,
                    maxLines = 1,
                    isError = isError,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        cursorColor = MaterialTheme.colorScheme.onPrimary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                        focusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.05f),
                        selectionColors = TextSelectionColors(
                            handleColor = MaterialTheme.colorScheme.onPrimary,
                            backgroundColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    )
                )
                if (isError) {
                    Text(
                        modifier = Modifier.align(Alignment.Start),
                        text = errorMessage,
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDismissRequest() },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Cancelar")
                }
                Button(
                    onClick = {
                        onConfirm(text.text)
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        color = Color.White
                    )
                }
            }
        }
    }
}
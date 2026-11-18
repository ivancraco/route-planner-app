package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.paging.PagingState
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import com.routeplanner.app.features.home.domain.model.RouteState
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.location.LocationCaptureState
import com.routeplanner.app.features.home.location.LocationCoordinates
import com.routeplanner.app.features.home.location.rememberLocationCaptureController
import com.swmansion.kmpmaps.core.Coordinates
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.location.COARSE_LOCATION
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

/* ------------------------------------------------------------------ */
/*  Estado compartido entre ambas pantallas                            */
/* ------------------------------------------------------------------ */

data class Note(val id: Long, val text: String)

class SharedState {
    private var nextId = 0L

    var draft by mutableStateOf("")
    val notes = mutableStateListOf<Note>()

    fun onDraftChange(value: String) {
        draft = value
    }

    fun addNote() {
        val text = draft.trim()
        if (text.isEmpty()) return
        notes.add(0, Note(nextId++, text))
        draft = ""
    }

    fun removeNote(note: Note) {
        notes.remove(note)
    }
}

/* ------------------------------------------------------------------ */
/*  Navegación                                                         */
/* ------------------------------------------------------------------ */

private data class NavItem(val title: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("Mapa", Icons.Default.LocationOn),
    NavItem("Detalles", Icons.AutoMirrored.Filled.List),
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    pagingState: PagingState<NotifierRouteSummary>,
    sharedState: SharedState = remember { SharedState() },
    userRoute: UserRoute?,
    routeStates: List<RouteState>,
    allRoutes: List<NotifierRouteSummary>,
    routePolyline: List<Coordinates> = emptyList(),
    createRouteFormState: CreateRouteFormState,
    onLoadMorePages: () -> Unit,
    onCreateRoute: (Double?, Double?) -> Unit,
    onChangeRoute: (String) -> Unit,
    onChangeRouteState: (String, Int) -> Unit,
    onUpdateRouteName: (String, String) -> Unit,
    onDeleteRoute: (String) -> Unit,
    onLocationCaptured: (Double, Double) -> Unit,
    onStopClick: (UserStop) -> Unit,
    onStopDetailScreen: (UserStop) -> Unit,
    //detalles screen
    onSearchStop: () -> Unit,
    onOptimizeRoute: () -> Unit,
    isOptimizing: Boolean,
    stopStates: List<StopState>,
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { navItems.size })
    val scope = rememberCoroutineScope()
    val panelState = rememberLeftPanelState()
    val locationCaptureController = rememberLocationCaptureController(Permission.COARSE_LOCATION)
    var isLocationEnabled by remember { mutableStateOf(false) }
    var coordinates by remember { mutableStateOf<LocationCoordinates?>(null) }
    var state by remember { mutableStateOf<LocationCaptureState>(LocationCaptureState.Idle) }

    val palette = rememberPalette()
    val listState = rememberLazyListState()

    val shouldMore by remember {
        derivedStateOf {
            val info = listState.layoutInfo
            val last = info.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= info.totalItemsCount - 10
        }
    }

    LaunchedEffect(
        shouldMore,
        pagingState.isLoading,
        pagingState.endReached,
        pagingState.error,
        pagingState.items.size
    ) {
        val canLoad =
            !pagingState.isLoading && !pagingState.endReached && pagingState.error == null && pagingState.items.isNotEmpty()
        if (canLoad && shouldMore) onLoadMorePages()
    }

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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            // targetPage => el icono cambia al instante al tocar o al deslizar,
                            // sin esperar a que termine la animación
                            selected = pagerState.targetPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                    if (index == 1)
                                        panelState.close()
                                }
                            },
                            icon = {
                                if (index == 1 && sharedState.notes.isNotEmpty()) {
                                    BadgedBox(badge = { Badge { Text("${sharedState.notes.size}") } }) {
                                        Icon(item.icon, contentDescription = item.title)
                                    }
                                } else {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            },
                            label = { Text(item.title) },
                        )
                    }
                }
            },
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = RoutePlannerTheme.dimens.spaceSm),
                beyondViewportPageCount = 1, // mantiene ambas páginas compuestas => transición fluida
                key = { it },
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> MapScreen(
                        userRoute = userRoute,
                        routeStates = routeStates,
                        allRoutes = allRoutes,
                        routePolyline = routePolyline,
                        onChangeState = onChangeRouteState,
                        onCreateRoute = onCreateRoute,
                        onChangeRoute = onChangeRoute,
                        onUpdateRouteName = onUpdateRouteName,
                        onDeleteRoute = onDeleteRoute,
                        onLocationCaptured = onLocationCaptured,
                        onStopClick = onStopClick,
                        onToggle = {
                            scope.launch {
                                panelState.toggle()
                            }
                        }
                    )

                    1 -> {
                        DetailScreen(
                            userRoute = userRoute,
                            onSearchStop = onSearchStop,
                            onOptimizeRoute = onOptimizeRoute,
                            isOptimizing = isOptimizing,
                            stopStates = stopStates,
                            onStopClick = onStopDetailScreen
                        )
                    }
                }
            }
        }

        LeftPanelOverlay(
            state = panelState,
            panelContent = {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(RoutePlannerTheme.colors.background)
                        .navigationBarsPadding()
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                    ) {
                        Text(
                            text = "Mis rutas",
                            style = RoutePlannerTheme.typography.titleLarge,
                            color = RoutePlannerTheme.colors.onPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start,
                        )

                        when {
                            pagingState.items.isEmpty() && pagingState.isLoading ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = palette.accent,
                                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeMd)
                                    )
                                }

                            pagingState.items.isEmpty() && pagingState.error != null ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(
                                        RoutePlannerTheme.dimens.spaceMd,
                                        Alignment.CenterVertically
                                    ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ocurrió un error. Intente nuevamente",
                                        style = RoutePlannerTheme.typography.bodyMedium,
                                        color = palette.textSecondary
                                    )
                                    Button(
                                        onClick = onLoadMorePages,
                                        shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceMd),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = palette.accent,
                                            contentColor = palette.onAccent
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                    ) {
                                        Text(
                                            text = "Reintentar",
                                            style = RoutePlannerTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium,
                                        )
                                    }
                                }

                            pagingState.items.isEmpty() ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.app_icon),
                                        "",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeLg)
                                    )
                                    Spacer(modifier = Modifier.height(RoutePlannerTheme.dimens.spaceXs))
                                    Text(
                                        text = "Tu lista de rutas está vacía",
                                        style = RoutePlannerTheme.typography.bodyMedium,
                                        color = palette.textSecondary,
                                    )
                                }

                            else -> LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                            ) {
                                items(pagingState.items) { route ->
                                    RouteSummaryCard(
                                        palette = palette,
                                        route = route,
                                        onRouteSelected = {
                                            scope.launch {
                                                //onDismissStopDetail()
                                                panelState.close()
                                            }
                                            onChangeRoute(route.id)
                                        }
                                    )
                                }

                                item {
                                    when {
                                        pagingState.isLoading ->
                                            Box(
                                                modifier = Modifier.fillMaxWidth(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    color = palette.accent,
                                                    modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm)
                                                )
                                            }

                                        pagingState.error != null ->
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                                            ) {
                                                Text(
                                                    text = "Error al cargar",
                                                    style = RoutePlannerTheme.typography.bodySmall,
                                                    color = palette.textSecondary
                                                )
                                                Button(
                                                    onClick = onLoadMorePages,
                                                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceMd),
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = palette.accent,
                                                        contentColor = palette.onAccent
                                                    ),
                                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                                ) {
                                                    Text(
                                                        text = "Reintentar",
                                                        style = RoutePlannerTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.Medium,
                                                    )
                                                }
                                            }
                                    }
                                }
                            }
                        }

                        /*if (allRoutes.isEmpty()) {
                            Text(
                                text = "No se encontraron resultados",
                                style = RoutePlannerTheme.typography.bodyMedium,
                                color = RoutePlannerTheme.colors.onPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = RoutePlannerTheme.dimens.spaceMd),
                                textAlign = TextAlign.Center,
                            )
                        }

                        LazyColumn(
                            modifier = Modifier.weight(1f),
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
                                            style = RoutePlannerTheme.typography.titleMedium,
                                            color = RoutePlannerTheme.colors.onPrimary
                                        )
                                        Text(
                                            text = route.createdAt.toArgentinaDateString(),
                                            style = RoutePlannerTheme.typography.labelMedium,
                                            color = RoutePlannerTheme.colors.onPrimary.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                    }
                                }
                            }
                        }*/

                        Button(
                            onClick = {
                                onCreateRoute(
                                    coordinates?.latitude,
                                    coordinates?.longitude
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoutePlannerTheme.colors.secondary,
                                contentColor = RoutePlannerTheme.colors.onSecondary,
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    "",
                                    tint = RoutePlannerTheme.colors.onSecondary
                                )
                                Text(
                                    text = "Nueva ruta",
                                    style = RoutePlannerTheme.typography.titleMedium,
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

/* ------------------------------------------------------------------ */
/*  Pantalla 1: escribe y agrega                                       */
/* ------------------------------------------------------------------ */

/*@Composable
private fun HomeScreen(state: SharedState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Inicio", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = state.draft,
            onValueChange = state::onDraftChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nueva nota") },
        )

        Button(
            onClick = state::addNote,
            enabled = state.draft.isNotBlank(),
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Agregar", modifier = Modifier.padding(start = 8.dp))
        }

        Text(
            text = "Notas guardadas: ${state.notes.size}",
            style = MaterialTheme.typography.bodyLarge,
        )

        state.notes.firstOrNull()?.let {
            Text(
                text = "Última: ${it.text}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}*/

/* ------------------------------------------------------------------ */
/*  Pantalla 2: lista las notas y permite borrar                       */
/* ------------------------------------------------------------------ */

@Composable
private fun NotesScreen(state: SharedState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Notas", style = MaterialTheme.typography.headlineMedium)

        // Muestra lo que se está escribiendo en la otra pantalla, en vivo
        if (state.draft.isNotBlank()) {
            Text(
                text = "Escribiendo en Inicio: ${state.draft}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (state.notes.isEmpty()) {
            Text("Aún no hay notas. Agrega una desde Inicio.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.notes, key = { it.id }) { note ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(note.text, modifier = Modifier.weight(1f))
                            IconButton(onClick = { state.removeNote(note) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteSummaryCard(
    palette: UiPalette,
    route: NotifierRouteSummary,
    onRouteSelected: (String) -> Unit
) {
    Card(
        onClick = { onRouteSelected(route.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceMd),
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(
            modifier = Modifier.padding(
                all = RoutePlannerTheme.dimens.spaceMd
            ),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = route.name,
                        style = RoutePlannerTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary,
                    )
                    Text(
                        text = route.state,
                        style = RoutePlannerTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textSecondary,
                    )
                }

                // Usuario como chip de acento suave
                Text(
                    text = route.createdAt.toArgentinaDateString(),
                    style = RoutePlannerTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = palette.accent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.accentSoft)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                        .align(Alignment.Top)
                )
            }
            /*Text(
                text = formatearFecha(route.date),
                style = RoutePlannerTheme.typography.bodyMedium,
                color = palette.textSecondary
            )*/
        }
    }
}
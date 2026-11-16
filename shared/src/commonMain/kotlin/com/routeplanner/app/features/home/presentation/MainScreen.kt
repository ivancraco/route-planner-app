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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.paging.PagingState
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.auth.data.local.SessionLocalDataSource
import com.routeplanner.app.features.home.domain.model.NotifierRouteSummary
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

@Composable
fun MainScreen(
    //sessionLocalDataSource: SessionLocalDataSource = koinInject(),
    state: PagingState<NotifierRouteSummary>,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onCreateRoute: () -> Unit,
    onRouteSelected: (String) -> Unit
) {
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
        state.isLoading,
        state.endReached,
        state.error,
        state.items.size
    ) {
        val canLoad =
            !state.isLoading && !state.endReached && state.error == null && state.items.isNotEmpty()
        if (canLoad && shouldMore) onLoadMore()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateRoute) {
                Icon(
                    Icons.Default.Add,
                    "Agregar nueva ruta"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(palette.background)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                    vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                ),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            /*Text(
                text = "Hola, ${sessionLocalDataSource.getSession()?.username}",
                style = RoutePlannerTheme.typography.headlineSmall,
                color = RoutePlannerTheme.colors.onPrimary
            )*/

            Text(
                text = "Mis rutas",
                style = RoutePlannerTheme.typography.titleLarge,
                color = RoutePlannerTheme.colors.onPrimary
            )

            when {
                state.items.isEmpty() && state.isLoading ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = palette.accent,
                            modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeMd)
                        )
                    }

                state.items.isEmpty() && state.error != null ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                            onClick = onRetry,
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

                state.items.isEmpty() ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                ) {
                    items(state.items) { route ->
                        RouteSummaryCard(
                            palette = palette,
                            route = route,
                            onRouteSelected = onRouteSelected
                        )
                    }

                    item {
                        when {
                            state.isLoading ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = palette.accent,
                                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm)
                                    )
                                }

                            state.error != null ->
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
                                        onClick = onRetry,
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
                Text(
                    text = route.name,
                    style = RoutePlannerTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                    modifier = Modifier.weight(1f)
                )
                // Usuario como chip de acento suave
                Text(
                    text = route.createdAt.toArgentinaDateString(),
                    style = RoutePlannerTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = palette.accent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.accentSoft)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = route.state + " · ",
                    style = RoutePlannerTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary,
                )
                Text(
                    text = if (route.stopsCount == 1) "1 parada" else "${route.stopsCount} paradas",
                    style = RoutePlannerTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = palette.textPrimary
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
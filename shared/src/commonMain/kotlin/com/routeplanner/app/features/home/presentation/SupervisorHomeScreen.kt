package com.routeplanner.app.features.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.paging.PagingState
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import org.jetbrains.compose.resources.painterResource
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorHomeScreen(
    state: PagingState<SupervisorRouteSummary>,
    supervisorRouteState: SupervisorRouteState,
    onLoadMore: () -> Unit,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onRouteSelected: (SupervisorRouteSummary) -> Unit
) {
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
            ),
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
    ) {
        var query by rememberSaveable { mutableStateOf("") }

        // Buscador
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            placeholder = {
                Text(
                    text = "Buscar...",
                    style = RoutePlannerTheme.typography.bodyLarge,
                )
            },
            onValueChange = {
                query = it
            },
            minLines = 1,
            maxLines = 1,
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusFull),
            colors = stopFieldColors(),
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (query.trim().isNotEmpty())
                            onSearch(query.trim())
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        "Buscador",
                        tint = palette.textSecondary
                    )
                }
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = query.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            query = ""
                            onSearch("")
                        }
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            "Limpiar búsqueda",
                            tint = palette.textSecondary
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        )

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
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
                            onClick = {
                                if (query.trim().isEmpty())
                                    onRetry()
                                else
                                    onSearch(query.trim())
                            },
                            shape = shape,
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
                        Text(
                            text = "No se encontraron resultados",
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
                        Card(
                            onClick = { onRouteSelected(route) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = shape,
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
                                        text = route.user,
                                        style = RoutePlannerTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = palette.accent,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(palette.accentSoft)
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                                Text(
                                    text = formatearFecha(route.date),
                                    style = RoutePlannerTheme.typography.bodyMedium,
                                    color = palette.textSecondary
                                )
                            }
                        }
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
                                        shape = shape,
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
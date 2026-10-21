package com.routeplanner.app.features.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import com.routeplanner.app.core.paging.PagingState
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary

@Composable
fun SupervisorHomeScreen(
    state: PagingState<SupervisorRouteSummary>,
    supervisorRouteState: SupervisorRouteState,
    onLoadMore: () -> Unit,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    onRouteSelected: (SupervisorRouteSummary) -> Unit
) {
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
            .background(RoutePlannerTheme.colors.primary)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(
                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
            ),
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
    ) {
        var query by rememberSaveable { mutableStateOf("") }

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            placeholder = {
                Text(
                    text = "Buscar...",
                    style = RoutePlannerTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            },
            onValueChange = {
                query = it
            },
            minLines = 1,
            maxLines = 1,
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusFull),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                unfocusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                unfocusedIndicatorColor = RoutePlannerTheme.colors.primaryContainer,
                focusedIndicatorColor = RoutePlannerTheme.colors.secondary,
                cursorColor = RoutePlannerTheme.colors.onPrimary
            ),
            leadingIcon = {
                IconButton(
                    onClick = {
                        if (query.trim().isNotEmpty())
                            onSearch(query.trim())
                    }
                ) {
                    Icon(
                        Icons.Default.Search,
                        "Buscador"
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
                            "Limpiar búsqueda"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        )

        when {
            state.items.isEmpty() && state.isLoading ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = RoutePlannerTheme.colors.secondary,
                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeMd)
                    )
                }

            state.items.isEmpty() && state.error != null ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ocurrió un error. Intente nuevamente",
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Button(
                        onClick = {
                            if (query.trim().isEmpty())
                                onRetry()
                            else
                                onSearch(query.trim())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutePlannerTheme.colors.secondary
                        )
                    ) {
                        Text(
                            text = "Reintentar",
                            style = RoutePlannerTheme.typography.titleMedium,
                            color = RoutePlannerTheme.colors.onSecondary
                        )
                    }
                }

            state.items.isEmpty() ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron resultados",
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                }

            else -> LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                items(state.items) {
                    ElevatedCard(
                        modifier = Modifier.clickable {
                            onRouteSelected(it)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                all = RoutePlannerTheme.dimens.spaceMd
                            ),
                            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = it.name,
                                    style = RoutePlannerTheme.typography.titleLarge,
                                    color = RoutePlannerTheme.colors.onPrimary
                                )
                                Text(
                                    text = it.user,
                                    style = RoutePlannerTheme.typography.bodyLarge,
                                    color = RoutePlannerTheme.colors.onPrimary
                                )
                            }
                            Text(
                                text = formatearFecha(it.date),
                                style = RoutePlannerTheme.typography.bodyMedium,
                                color = RoutePlannerTheme.colors.onPrimary
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
                                    color = RoutePlannerTheme.colors.secondary,
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
                                    color = RoutePlannerTheme.colors.onPrimary
                                )
                                Button(
                                    onClick = onRetry,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = RoutePlannerTheme.colors.secondary
                                    )
                                ) {
                                    Text(
                                        text = "Reintentar",
                                        style = RoutePlannerTheme.typography.titleSmall,
                                        color = RoutePlannerTheme.colors.onSecondary
                                    )
                                }
                            }
                    }
                }
            }
        }

        /*when (supervisorRouteState) {

            is SupervisorRouteState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = RoutePlannerTheme.colors.secondary,
                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeMd)
                    )
                }
            }

            is SupervisorRouteState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ocurrió un error. Intente nuevamente",
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Button(
                        onClick = {
                            if (query.trim().isEmpty())
                                onRetry()
                            else
                                onSearch(query.trim())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutePlannerTheme.colors.secondary
                        )
                    ) {
                        Text(
                            text = "Reintentar",
                            style = RoutePlannerTheme.typography.titleMedium,
                            color = RoutePlannerTheme.colors.onSecondary
                        )
                    }
                }
            }

            is SupervisorRouteState.Success -> {
                if (supervisorRouteState.data.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron resultados",
                            style = RoutePlannerTheme.typography.bodyMedium,
                            color = RoutePlannerTheme.colors.onPrimary
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                ) {
                    items(supervisorRouteState.data) {
                        ElevatedCard(
                            modifier = Modifier.clickable {
                                onRouteSelected(it)
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    all = RoutePlannerTheme.dimens.spaceMd
                                ),
                                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = it.name,
                                        style = RoutePlannerTheme.typography.titleLarge,
                                        color = RoutePlannerTheme.colors.onPrimary
                                    )
                                    Text(
                                        text = it.user,
                                        style = RoutePlannerTheme.typography.bodyLarge,
                                        color = RoutePlannerTheme.colors.onPrimary
                                    )
                                }
                                Text(
                                    text = formatearFecha(it.date),
                                    style = RoutePlannerTheme.typography.bodyMedium,
                                    color = RoutePlannerTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            else -> {}
        }*/
    }
}
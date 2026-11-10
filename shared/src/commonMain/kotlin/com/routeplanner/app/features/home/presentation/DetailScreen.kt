package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import org.jetbrains.compose.resources.painterResource
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

@Composable
fun DetailScreen(
    userRoute: UserRoute?,
    onSearchStop: () -> Unit,
    onOptimizeRoute: () -> Unit,
    isOptimizing: Boolean,
    stopStates: List<StopState>,
    onStopClick: (UserStop) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal),
    ) {
        if (userRoute != null) {
            val shortAddress = userRoute.originDir
                .split(",")
                .firstOrNull()
                ?.trim() ?: userRoute.originDir

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(
                        all = RoutePlannerTheme.dimens.spaceMd
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = userRoute.name,
                            style = RoutePlannerTheme.typography.headlineSmall,
                            color = RoutePlannerTheme.colors.onPrimary,
                        )
                        Text(
                            text = userRoute.createdAt.toArgentinaDateString(),
                            style = RoutePlannerTheme.typography.titleSmall,
                            color = RoutePlannerTheme.colors.onPrimary,
                        )
                    }
                    Text(
                        text = "Origen · $shortAddress",
                        style = RoutePlannerTheme.typography.titleMedium,
                        color = RoutePlannerTheme.colors.onPrimary,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                // Buscador de parada
                Button(
                    onClick = onSearchStop,
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoutePlannerTheme.colors.surfaceVariant,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Agregar parada",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // Optimizar — solo si hay al menos 2 paradas
                if ((userRoute.userStops.size) >= 2) {
                    Button(
                        onClick = onOptimizeRoute,
                        modifier = Modifier
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoutePlannerTheme.colors.secondary.copy(alpha = 0.15f),
                            contentColor = RoutePlannerTheme.colors.secondary
                        ),
                        enabled = !isOptimizing
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

            // Lista de paradas
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                items(userRoute.userStops.sortedBy { it.order }, key = { it.id }) { stop ->
                    StopCard(stop = stop, state = stopStates, onStopDetailScreen = onStopClick)
                    /*StopRow(
                        stop = stop,
                        state = stopStates,
                        markerColor = MarkerColors.waypoint,
                        onClick = { onStopClick(stop) },
                        labelContent = {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(25.dp)
                                        .width(2.dp)
                                        .background(Color.Blue)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Color.Blue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${stop.order}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .height(25.dp)
                                        .width(2.dp)
                                        .background(
                                            if (stop.order < userRoute.userStops.size) Color.Blue
                                            else Color.Transparent
                                        )
                                )
                            }
                        }
                    )*/
                }
            }
        } else {
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
                    text = "No tienes una ruta activa",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = RoutePlannerTheme.colors.onPrimary,
                )
            }
        }
    }
}

@Composable
fun StopCard(
    stop: UserStop,
    state: List<StopState>,
    onStopDetailScreen: (UserStop) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStopDetailScreen(stop)
            },
        shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
    ) {
        val shortAddress = stop.direction
            .split(",")
            .firstOrNull()
            ?.trim() ?: stop.direction

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = RoutePlannerTheme.dimens.spaceMd)
                .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Blue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${stop.order}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                )
            }
            Column {
                Text(
                    text = shortAddress,
                    style = RoutePlannerTheme.typography.titleLarge,
                    color = RoutePlannerTheme.colors.onPrimary
                )
                Row {
                    Text(
                        text = stop.recipient + " · ",
                        style = RoutePlannerTheme.typography.titleSmall,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = state.find { it.id == stop.stateId }?.description ?: "",
                        style = RoutePlannerTheme.typography.titleSmall,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                }
            }
        }

    }
}
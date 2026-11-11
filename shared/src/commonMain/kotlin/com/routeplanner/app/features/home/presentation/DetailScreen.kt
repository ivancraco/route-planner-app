package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.DarkPalette
import com.routeplanner.app.core.ui.LightPalette
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import org.jetbrains.compose.resources.painterResource
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

@Composable
private fun rememberPalette(): UiPalette {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (isDark) DarkPalette else LightPalette
}

@Composable
fun DetailScreen(
    userRoute: UserRoute?,
    onSearchStop: () -> Unit,
    onOptimizeRoute: () -> Unit,
    isOptimizing: Boolean,
    stopStates: List<StopState>,
    onStopClick: (UserStop) -> Unit,
) {
    val palette = rememberPalette()
    val cardShape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)

    Column(
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal),
    ) {
        if (userRoute != null) {
            val shortAddress = userRoute.originDir
                .split(",")
                .firstOrNull()
                ?.trim() ?: userRoute.originDir

            // Cabecera de la ruta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = cardShape,
                colors = CardDefaults.cardColors(containerColor = palette.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, palette.border),
            ) {
                Column(
                    modifier = Modifier.padding(all = RoutePlannerTheme.dimens.spaceMd)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = userRoute.name,
                            style = RoutePlannerTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary,
                        )
                        Text(
                            text = userRoute.createdAt.toArgentinaDateString(),
                            style = RoutePlannerTheme.typography.titleSmall,
                            fontWeight = FontWeight.Normal,
                            color = palette.textSecondary,
                        )
                    }
                    Text(
                        text = "Origen · $shortAddress",
                        style = RoutePlannerTheme.typography.titleMedium,
                        fontWeight = FontWeight.Normal,
                        color = palette.textSecondary,
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                // Acción principal: agregar parada
                Button(
                    onClick = onSearchStop,
                    modifier = Modifier.weight(1f),
                    shape = cardShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = palette.accent,
                        contentColor = palette.onAccent,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Agregar parada",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
                // Acción secundaria: optimizar — solo si hay al menos 2 paradas
                if (userRoute.userStops.size >= 2) {
                    OutlinedButton(
                        onClick = onOptimizeRoute,
                        modifier = Modifier.weight(1f),
                        shape = cardShape,
                        border = BorderStroke(1.dp, palette.border),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.accent,
                        ),
                        enabled = !isOptimizing
                    ) {
                        if (isOptimizing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = palette.accent
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Route,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Optimizar ruta",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                            )
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
                    color = palette.textSecondary,
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
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)

    val shortAddress = stop.direction
        .split(",")
        .firstOrNull()
        ?.trim() ?: stop.direction

    val stateDescription = state.find { it.id == stop.stateId }?.description ?: ""

    // El color del estado comunica información: ámbar = pendiente, verde = notificado.
    val normalized = stateDescription.lowercase()
    val statusColor = when {
        normalized.isBlank() -> palette.textSecondary
        normalized.contains("no notific") -> palette.warning
        normalized.contains("notific") -> palette.success
        else -> palette.textSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStopDetailScreen(stop) },
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = palette.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, palette.border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = RoutePlannerTheme.dimens.spaceMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Número de parada: fondo de acento suave + número en acento
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(palette.accentSoft),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${stop.order}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.accent,
                )
            }
            Column {
                Text(
                    text = shortAddress,
                    style = RoutePlannerTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stop.recipient,
                        style = RoutePlannerTheme.typography.titleSmall,
                        fontWeight = FontWeight.Normal,
                        color = palette.textSecondary
                    )
                    if (stateDescription.isNotBlank()) {
                        /*Text(
                            text = "·",
                            style = RoutePlannerTheme.typography.titleSmall,
                            color = palette.textSecondary
                        )*/
                        // Punto de estado
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = stateDescription,
                            style = RoutePlannerTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = statusColor
                        )
                    }
                }
            }
        }
    }
}
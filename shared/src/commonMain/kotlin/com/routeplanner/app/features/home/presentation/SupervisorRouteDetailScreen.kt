package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary

@Composable
fun SupervisorRouteDetailScreen(
    route: SupervisorRouteSummary,
    onBack: () -> Unit
) {
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)

    val shortRouteAddress = remember {
        route.origin
            .split(",")
            .firstOrNull()
            ?.trim() ?: route.origin
    }

    val sortedStops = remember(route.stops) { route.stops.sortedBy { it.order } }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles",
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver",
                            tint = palette.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.background,
                    scrolledContainerColor = palette.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = RoutePlannerTheme.dimens.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceLg)
        ) {
            // Resumen de la ruta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                colors = CardDefaults.cardColors(containerColor = palette.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, palette.border),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = RoutePlannerTheme.dimens.spaceMd),
                    verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = route.user,
                        style = RoutePlannerTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                    Row() {
                        Text(
                            text = "${route.name} · ",
                            style = RoutePlannerTheme.typography.bodyMedium,
                            color = palette.textSecondary
                        )
                        Text(
                            text = route.state,
                            style = RoutePlannerTheme.typography.bodyMedium,
                            color = palette.textSecondary
                        )
                    }
                    Text(
                        text = formatearFecha(route.date),
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = palette.textSecondary
                    )
                    // Chip con la cantidad de paradas
                    Text(
                        text = if (route.stops.size == 1) "1 parada" else "${route.stops.size} paradas",
                        style = RoutePlannerTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = palette.accent,
                        modifier = Modifier
                            .padding(top = RoutePlannerTheme.dimens.spaceXs)
                            .clip(CircleShape)
                            .background(palette.accentSoft)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Recorrido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                Text(
                    text = "Recorrido",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = palette.textSecondary
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    // Origen
                    item {
                        TimelineRow(
                            palette = palette,
                            isFirst = true,
                            isLast = sortedStops.isEmpty(),
                            node = {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(palette.accent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = palette.onAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = shortRouteAddress,
                                style = RoutePlannerTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Text(
                                text = "Origen",
                                style = RoutePlannerTheme.typography.bodySmall,
                                color = palette.textSecondary
                            )
                        }
                    }
                    // Paradas
                    itemsIndexed(sortedStops) { index, stop ->
                        val shortStopAddress =
                            stop.direction
                                .split(",")
                                .firstOrNull()
                                ?.trim() ?: stop.direction
                        val statusColor = stopStatusColor(palette, stop.state)

                        TimelineRow(
                            palette = palette,
                            isFirst = false,
                            isLast = index == sortedStops.lastIndex,
                            node = {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(palette.accentSoft),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${stop.order}",
                                        style = RoutePlannerTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.accent,
                                    )
                                }
                            }
                        ) {
                            Text(
                                text = shortStopAddress,
                                style = RoutePlannerTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = palette.textPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(statusColor)
                                )
                                Text(
                                    text = stop.state,
                                    style = RoutePlannerTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = statusColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fila del timeline: riel a la izquierda (línea + nodo) y tarjeta con el contenido a la derecha.
@Composable
private fun TimelineRow(
    palette: UiPalette,
    isFirst: Boolean,
    isLast: Boolean,
    node: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val lineColor = palette.border
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Riel
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .width(2.dp)
                    .background(if (isFirst) Color.Transparent else lineColor)
            )
            node()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .width(2.dp)
                    .background(if (isLast) Color.Transparent else lineColor)
            )
        }
        // Contenido
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 12.dp),
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, palette.border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXxs)
            ) {
                content()
            }
        }
    }
}

// El color comunica el estado: ámbar = pendiente, verde = ok, rojo = problema.
private fun stopStatusColor(palette: UiPalette, state: String): Color {
    val s = state.lowercase()
    return when {
        s.isBlank() -> palette.textSecondary
        s.contains("no notific") || s.contains("pendiente") -> palette.warning
        s.contains("fall") || s.contains("no recib") -> palette.danger
        s.contains("notific") || s.contains("exit") || s.contains("entreg") -> palette.success
        else -> palette.textSecondary
    }
}
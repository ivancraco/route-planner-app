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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary

@Composable
fun SupervisorRouteDetailScreen(
    route: SupervisorRouteSummary,
    onBack: () -> Unit
) {
    val shortRouteAddress = remember {
        route.origin
            .split(",")
            .firstOrNull()
            ?.trim() ?: route.origin
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = RoutePlannerTheme.colors.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(RoutePlannerTheme.colors.primary)
                .padding(all = RoutePlannerTheme.dimens.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceLg)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = RoutePlannerTheme.dimens.spaceMd),
                    verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = route.user,
                        style = RoutePlannerTheme.typography.titleLarge,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = route.name,
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                    Text(
                        text = formatearFecha(route.date),
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(25.dp)
                                    .width(2.dp)
                                    .background(Color.Transparent)
                            )
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Blue),
                            )
                            Box(
                                modifier = Modifier.width(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height(25.dp)
                                        .width(2.dp)
                                        .background(Color.Blue)
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .width(RoutePlannerTheme.dimens.spaceSm)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                RoutePlannerTheme.dimens.spaceXxs
                            )
                        ) {
                            Text(
                                text = shortRouteAddress,
                                style = RoutePlannerTheme.typography.titleMedium,
                                color = RoutePlannerTheme.colors.onPrimary
                            )
                            Text(
                                text = "Origen",
                                style = RoutePlannerTheme.typography.bodySmall,
                                color = RoutePlannerTheme.colors.onPrimary
                            )
                        }
                    }
                }
                items(route.stops.sortedBy { it.order }) { stop ->
                    val shortStopAddress =
                        stop.direction
                            .split(",")
                            .firstOrNull()
                            ?.trim() ?: stop.direction

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                        if (stop.order != null && stop.order < route.stops.size) Color.Blue
                                        else Color.Transparent
                                    )
                            )
                        }
                        Spacer(
                            modifier = Modifier
                                .width(RoutePlannerTheme.dimens.spaceSm)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(
                                RoutePlannerTheme.dimens.spaceXxs
                            )
                        ) {
                            Text(
                                text = shortStopAddress,
                                style = RoutePlannerTheme.typography.titleMedium,
                                color = RoutePlannerTheme.colors.onPrimary
                            )
                            Text(
                                text = stop.state,
                                style = RoutePlannerTheme.typography.bodySmall,
                                color = RoutePlannerTheme.colors.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
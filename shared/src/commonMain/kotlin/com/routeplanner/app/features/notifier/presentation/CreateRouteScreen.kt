package com.routeplanner.app.features.notifier.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.routeplanner.app.features.notifier.data.model.NotifierRouteEntity
import com.routeplanner.app.features.notifier.location.LocationCoordinates
import com.routeplanner.app.ui.RoutePlannerTheme
import kotlin.time.Clock

@Composable
fun CreateRouteScreen(
    originLatitude: Double?,
    originLongitude: Double?,
    destinationLatitude: Double?,
    destinationLongitude: Double?,
    onDismiss: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onPickAddress: (forOrigin: Boolean) -> Unit,
    onCreateRoute: (NotifierRouteEntity) -> Unit,
) {
    var showSelectionDialog by remember { mutableStateOf(false) }
    var showFindDirectionsDialog by remember { mutableStateOf(false) }
    var isLocationPermissionGranted by remember { mutableStateOf(originLatitude != null && originLongitude != null) }
    var state by remember {
        mutableStateOf(
            CreateRouteState(
                origin = initialSelectionFor(
                    originLatitude,
                    originLongitude
                ),
                destination = initialSelectionFor(
                    originLatitude,
                    originLongitude
                ),
            )
        )
    }

    // Si el permiso se concede mientras el diálogo está abierto (el usuario
    // vuelve de Settings, por ejemplo), promovemos automáticamente el
    // selector de Origen a "usando ubicación actual" — solo si todavía
    // no eligió una dirección manual a propósito.
    LaunchedEffect(isLocationPermissionGranted, originLatitude, originLongitude) {
        if (isLocationPermissionGranted && originLatitude != null && originLongitude != null &&
            state.origin is LocationSelection.PermissionRequired
        ) {
            state = state.copy(
                origin = LocationSelection.UsingCurrentLocation(
                    LocationCoordinates(
                        latitude = originLatitude,
                        longitude = originLongitude
                    )
                ),
                destination = LocationSelection.UsingCurrentLocation(
                    LocationCoordinates(
                        latitude = originLatitude,
                        longitude = originLatitude
                    )
                ),
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoutePlannerTheme.colors.primary)
            .statusBarsPadding()
            .padding(
                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
            ),
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXl),
    ) {
        // ── Header: título + X ──────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Nueva ruta",
                color = RoutePlannerTheme.colors.onPrimary,
                style = RoutePlannerTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center),
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(RoutePlannerTheme.dimens.iconSizeMd),
            ) {

            }
        }

        // ── Nombre ───────────────────────────────────────────────
        RouteTextField(
            value = state.name,
            onValueChange = { state = state.copy(name = it) },
        )

        // ── Origen ───────────────────────────────────────────────
        LocationSelectorField(
            label = "Origen",
            selection = state.origin,
            onUseCurrentLocation = {
                state =
                    if (isLocationPermissionGranted && originLatitude != null && originLongitude != null) {
                        state.copy(
                            origin = LocationSelection.UsingCurrentLocation(
                                LocationCoordinates(
                                    originLatitude,
                                    originLongitude
                                )
                            ),
                        )
                    } else {
                        state.copy(origin = LocationSelection.PermissionRequired)
                    }
            },
            onRequestPermission = onRequestLocationPermission,
            onPickAddress = { onPickAddress(true) },
            onSelectionChange = { showSelectionDialog = true }
        )

        // ── Destino ──────────────────────────────────────────────
        LocationSelectorField(
            label = "Destino",
            selection = state.destination,
            onUseCurrentLocation = {
                state =
                    if (isLocationPermissionGranted && originLatitude != null && originLongitude != null) {
                        state.copy(
                            destination = LocationSelection.UsingCurrentLocation(
                                LocationCoordinates(
                                    originLatitude,
                                    originLongitude
                                )
                            ),
                        )
                    } else {
                        state.copy(destination = LocationSelection.PermissionRequired)
                    }
            },
            onRequestPermission = onRequestLocationPermission,
            onPickAddress = { onPickAddress(false) },
            onSelectionChange = { showSelectionDialog = true }
        )

        // ── Botón crear ──────────────────────────────────────────
        Button(
            onClick = {
                val originCoordinates = state.origin.coordinatesOrNull()
                val destinationCoordinates = state.destination.coordinatesOrNull()
                if (state.isValid && originCoordinates != null && destinationCoordinates != null) {
                    val notifierRouteEntity = NotifierRouteEntity(
                        userId = 1,
                        stateId = 1,
                        name = state.name,
                        createdAt = Clock.System.now(),
                        originDir = "Origen",
                        originLatitude = originCoordinates.latitude,
                        originLongitude = originCoordinates.longitude,
                        destinationDir = "Destino",
                        destinationLatitude = destinationCoordinates.latitude,
                        destinationLongitude = destinationCoordinates.longitude,
                    )
                    onCreateRoute(notifierRouteEntity)
                    onDismiss()
                }
            },
            enabled = state.isValid,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(RoutePlannerTheme.dimens.buttonHeight)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoutePlannerTheme.colors.secondary,
            ),
        ) {
            Text(
                text = "Crear ruta",
                style = RoutePlannerTheme.typography.titleMedium,
                color = RoutePlannerTheme.colors.onSecondary,
            )
        }

        if (showSelectionDialog) {
            LocationSelectionDialog(
                onDismiss = { showSelectionDialog = false },
                onLocationSelected = {
                    showSelectionDialog = false
                },
                onFindDirections = {
                    showSelectionDialog = false
                    showFindDirectionsDialog = true
                }
            )
        }

        if (showFindDirectionsDialog) {
            FindDirectionsScreen(
                onDismiss = { showFindDirectionsDialog = false },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Decide la selección inicial de Origen al abrir el diálogo
// ---------------------------------------------------------------------------

private fun initialSelectionFor(
    latitude: Double?,
    longitude: Double?,
): LocationSelection = when {
    latitude != null && longitude != null ->
        LocationSelection.UsingCurrentLocation(LocationCoordinates(latitude, longitude))

    else -> LocationSelection.PermissionRequired
}

// ---------------------------------------------------------------------------
// Campo de texto simple (Nombre)
// ---------------------------------------------------------------------------

@Composable
private fun RouteTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Nombre de la ruta",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = RoutePlannerTheme.colors.primary.copy(alpha = 0.5f),
            focusedContainerColor = RoutePlannerTheme.colors.primary.copy(alpha = 0.5f),
            unfocusedBorderColor = RoutePlannerTheme.colors.secondary.copy(alpha = 0.6f),
            focusedBorderColor = RoutePlannerTheme.colors.secondary,
            cursorColor = RoutePlannerTheme.colors.onPrimary,
        ),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
    )
}
@Composable
private fun LocationSelectorField(
    label: String,
    selection: LocationSelection,
    onUseCurrentLocation: () -> Unit,
    onRequestPermission: () -> Unit,
    onPickAddress: () -> Unit,
    onSelectionChange: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
    ) {
        Text(
            text = label,
            style = RoutePlannerTheme.typography.bodyMedium,
            color = RoutePlannerTheme.colors.onPrimary,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = RoutePlannerTheme.dimens.spaceXxl)
                .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
                .background(RoutePlannerTheme.colors.primaryContainer)
                .padding(horizontal = RoutePlannerTheme.dimens.spaceSm)
                .clickable { onSelectionChange() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
        ) {
            when (selection) {
                is LocationSelection.UsingCurrentLocation -> {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary,
                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm),
                    )
                    Text(
                        text = "Usando ubicación actual",
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary,
                    )
                }

                is LocationSelection.ManualAddress -> {
                    Text(
                        text = selection.address,
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary,
                    )
                }

                LocationSelection.PermissionRequired -> {
                    Text(
                        text = "Habilitar ubicación",
                        style = RoutePlannerTheme.typography.bodySmall,
                        color = RoutePlannerTheme.colors.onPrimary,
                        modifier = Modifier.clickable { onRequestPermission() },
                    )
                }

                LocationSelection.Empty -> {
                    Text(
                        text = "Seleccionar una opción",
                        style = RoutePlannerTheme.typography.bodyMedium,
                        color = RoutePlannerTheme.colors.onPrimary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionDialog(
    onDismiss: () -> Unit,
    onLocationSelected: () -> Unit,
    onFindDirections: () -> Unit
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
                        onLocationSelected()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
                Text(
                    text = "Usar ubicación actual",
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
                        onFindDirections()
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
                Text(
                    text = "Buscar direcciones",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = RoutePlannerTheme.colors.onPrimary,
                )
            }
        }
    }
}
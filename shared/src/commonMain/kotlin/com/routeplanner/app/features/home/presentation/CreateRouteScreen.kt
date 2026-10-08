package com.routeplanner.app.features.home.presentation

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.AddressSearchState
import com.routeplanner.app.features.home.location.LocationCoordinates
import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.SelectedAddress
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateRouteScreen(
    placesState: AddressSearchState,
    formState: CreateRouteFormState,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onDateChange: (Instant) -> Unit,
    onOriginSelected: (String, String?, Double, Double) -> Unit,
    onCreateRoute: () -> Unit,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onClearPlacesState: () -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
) {
    var showAddressSearch by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val tz = TimeZone.of("America/Argentina/Buenos_Aires")
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = formState.selectedDate
            .toLocalDateTime(tz)
            .date
            .atStartOfDayIn(tz)
            .toEpochMilliseconds()
    )

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
        // Header
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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
            }
        }

        // Nombre
        OutlinedTextField(
            value = formState.name,
            onValueChange = onNameChange,
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
                focusedTextColor = RoutePlannerTheme.colors.onPrimary,
                unfocusedTextColor = RoutePlannerTheme.colors.onPrimary,
            ),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        // Origen
        Column(
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {
            Text(
                text = "Origen",
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
                    .clickable { showAddressSearch = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                if (formState.isResolvingOrigin) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RoutePlannerTheme.colors.onPrimary,
                        modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm),
                    )
                }
                val shortAddress = formState.originDir
                    .split(",")
                    .firstOrNull()
                    ?.trim() ?: formState.originDir
                Text(
                    text = shortAddress.ifBlank { "Seleccionar origen" },
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = if (formState.originDir.isBlank())
                        RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
                    else
                        RoutePlannerTheme.colors.onPrimary,
                )
            }
        }

        // Fecha
        Column(
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {
            Text(
                text = "Fecha",
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
                    .clickable { showDatePicker = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = RoutePlannerTheme.colors.onPrimary,
                    modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm),
                )
                Text(
                    text = formState.selectedDate.toArgentinaDateString(),
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = RoutePlannerTheme.colors.onPrimary,
                )
            }
        }

        // Botón crear
        Button(
            onClick = {
                onCreateRoute()
                onDismiss()
            },
            enabled = formState.isValid && !formState.isResolvingOrigin,
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
    }

    // Dialog búsqueda de origen
    if (showAddressSearch) {
        AddressSearchField(
            state = placesState,
            onAddressSelected = { suggestion, selected ->
                onOriginSelected(
                    suggestion.primaryText,
                    suggestion.placeId,
                    selected.latitude,
                    selected.longitude
                )
                showAddressSearch = false
            },
            onValueChange = onValueChange,
            clear = onClear,
            onSuggestionSelected = onSuggestionSelected,
            onDismiss = {
                onClearPlacesState()
                showAddressSearch = false
            },
            label = "Buscar origen"
        )
    }

    // DatePicker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onDateChange(Instant.fromEpochMilliseconds(millis))
                    }
                    showDatePicker = false
                }) {
                    Text(
                        text = "Confirmar",
                        color = RoutePlannerTheme.colors.secondary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Cancelar",
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = RoutePlannerTheme.colors.primaryContainer,
                    titleContentColor = RoutePlannerTheme.colors.onPrimary,
                    headlineContentColor = RoutePlannerTheme.colors.onPrimary,
                    weekdayContentColor = RoutePlannerTheme.colors.onPrimary,
                    selectedDayContainerColor = RoutePlannerTheme.colors.secondary,
                    todayDateBorderColor = RoutePlannerTheme.colors.secondary,
                    dayContentColor = RoutePlannerTheme.colors.onPrimary,
                )
            )
        }
    }
}

// Formatea Instant a string legible en español
fun Instant.toLocalDateString(): String {
    val millis = toEpochMilliseconds()
    val days = millis / 86_400_000L
    // simple ISO — reemplazá con tu librería de fecha si ya la tenés
    return Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .date
        .toString()  // "2025-08-27"
}

/*@OptIn(ExperimentalUuidApi::class)
@Composable
fun CreateRouteScreen(
    placesState: AddressSearchState,
    originLatitude: Double = -31.39676517404372,
    originLongitude: Double = -58.01713884030075,
    onDismiss: () -> Unit,
    onRequestLocationPermission: () -> Unit,
    onPickAddress: (forOrigin: Boolean) -> Unit,
    onFindDirections: (String) -> Unit,
    onCreateRoute: (UserRoute) -> Unit,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
)
{
    var addressType by remember { mutableStateOf("") }
    var showSelectionDialog by remember { mutableStateOf(false) }
    var showFindDirectionsDialog by remember { mutableStateOf(false) }
    var isLocationPermissionGranted by remember { mutableStateOf(true) }
    var state by remember {
        mutableStateOf(
            CreateRouteState(
                origin = ManualAddress(
                    address = "Municipalidad de Concordia",
                    coordinates = LocationCoordinates(
                        latitude = originLatitude,
                        longitude = originLongitude
                    )
                ),
                destination = ManualAddress(
                    address = "Municipalidad de Concordia",
                    coordinates = LocationCoordinates(
                        latitude = originLatitude,
                        longitude = originLongitude
                    )
                ),
            )
        )
    }

    // Si el permiso se concede mientras el diálogo está abierto (el usuario
    // vuelve de Settings, por ejemplo), promovemos automáticamente el
    // selector de Origen a "usando ubicación actual" — solo si todavía
    // no eligió una dirección manual a propósito.
    /*LaunchedEffect(isLocationPermissionGranted, originLatitude, originLongitude) {
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
    }*/

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
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = RoutePlannerTheme.colors.onPrimary,
                )
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
            selection = state.origin.address,
            onUseCurrentLocation = {
                /*state =
                    if (isLocationPermissionGranted) {
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
                    }*/
            },
            onRequestPermission = onRequestLocationPermission,
            onPickAddress = { onPickAddress(true) },
            onSelectionChange = {
                //showSelectionDialog = true
                showFindDirectionsDialog = true
                addressType = AddressType.ROUTE_ORIGIN.type
            }
        )

        // ── Destino ──────────────────────────────────────────────
        LocationSelectorField(
            label = "Destino",
            selection = state.destination.address,
            onUseCurrentLocation = {
                /*state =
                    if (isLocationPermissionGranted) {
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
                    }*/
            },
            onRequestPermission = onRequestLocationPermission,
            onPickAddress = { onPickAddress(false) },
            onSelectionChange = {
                //showSelectionDialog = true
                showFindDirectionsDialog = true
                addressType = AddressType.ROUTE_DESTINATION.type
            }
        )

        // ── Botón crear ──────────────────────────────────────────
        Button(
            onClick = {
                val originCoordinates = state.origin.coordinates
                val destinationCoordinates = state.destination.coordinates
                if (state.isValid) {
                    val createUserRoute = UserRoute(
                        id = generateId(),
                        name = state.name,
                        state = RouteStateEnum.ACTIVE.description,
                        createdAt = Clock.System.now(),
                        originDir = state.origin.address,
                        originLatitude = originCoordinates.latitude,
                        originLongitude = originCoordinates.longitude,
                        destinationDir = state.destination.address,
                        destinationLatitude = destinationCoordinates.latitude,
                        destinationLongitude = destinationCoordinates.longitude,
                    )
                    onCreateRoute(createUserRoute)
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
                    //onFindDirections(addressType)
                }
            )
        }

        if (showFindDirectionsDialog) {
            AddressSearchField(
                //addressType = addressType,
                state = placesState,
                onAddressSelected = { suggestion, selected ->
                    when (addressType) {
                        AddressType.ROUTE_ORIGIN.type -> {
                            state = state.copy(
                                origin = ManualAddress(
                                    address = suggestion.primaryText,
                                    coordinates = LocationCoordinates(
                                        latitude = selected.latitude,
                                        longitude = selected.longitude
                                    )
                                )
                            )
                        }

                        AddressType.ROUTE_DESTINATION.type -> {
                            state = state.copy(
                                destination = ManualAddress(
                                    address = suggestion.primaryText,
                                    coordinates = LocationCoordinates(
                                        latitude = selected.latitude,
                                        longitude = selected.longitude
                                    )
                                )
                            )
                        }
                    }
                },
                onValueChange = {
                    onValueChange(it)
                },
                clear = {
                    onClear()
                },
                onSuggestionSelected = onSuggestionSelected,
                onDismiss = { showFindDirectionsDialog = false }
            )
        }
    }
}*/

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
    selection: String,
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
            /*when (selection) {
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
            }*/
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = RoutePlannerTheme.colors.onPrimary,
                modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm),
            )
            Text(
                text = selection,
                style = RoutePlannerTheme.typography.bodyMedium,
                color = RoutePlannerTheme.colors.onPrimary,
            )
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
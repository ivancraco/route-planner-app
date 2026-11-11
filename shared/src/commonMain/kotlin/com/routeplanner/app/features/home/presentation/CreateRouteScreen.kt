package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.common.toArgentinaDateString
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.rememberPalette
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
    onNavigateToAddressSearchScreen: () -> Unit
) {
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)

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
            .background(palette.background)
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
                color = palette.textPrimary,
                style = RoutePlannerTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
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
                    tint = palette.textPrimary,
                )
            }
        }

        // Nombre
        OutlinedTextField(
            value = formState.name,
            onValueChange = onNameChange,
            placeholder = { Text(text = "Nombre de la ruta") },
            modifier = Modifier.fillMaxWidth(),
            shape = shape,
            singleLine = true,
            colors = stopFieldColors(),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        // Origen
        Column(
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {
            SectionLabel("Origen")
            val shortAddress = formState.originDir
                .split(",")
                .firstOrNull()
                ?.trim() ?: formState.originDir
            SelectorRow(
                icon = Icons.Default.LocationOn,
                text = shortAddress.ifBlank { "Seleccionar origen" },
                isPlaceholder = formState.originDir.isBlank(),
                isLoading = formState.isResolvingOrigin,
                onClick = {
                    if (!formState.isResolvingOrigin)
                        onNavigateToAddressSearchScreen()
                }
            )
        }

        // Fecha
        Column(
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
        ) {
            SectionLabel("Fecha")
            SelectorRow(
                icon = Icons.Default.CalendarMonth,
                text = formState.selectedDate.toArgentinaDateString(),
                onClick = { showDatePicker = true }
            )
        }

        // Botón crear
        val canCreate = formState.isValid && !formState.isResolvingOrigin
        Button(
            onClick = {
                onCreateRoute()
                onDismiss()
            },
            enabled = canCreate,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = shape,
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.onAccent,
                disabledContainerColor = palette.border,
                disabledContentColor = palette.textSecondary.copy(alpha = 0.5f),
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        ) {
            Text(
                text = "Crear ruta",
                style = RoutePlannerTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
            )
        }
    }

    // Dialog búsqueda de origen
    /*if (showAddressSearch) {
        AddressSearchField2(
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
            onClear = onClear,
            onSuggestionSelected = onSuggestionSelected,
            onDismiss = {
                onClearPlacesState()
                showAddressSearch = false
            },
            //label = "Buscar origen"
        )
    }*/

    // DatePicker dialog
    if (showDatePicker) {
        val pickerColors = DatePickerDefaults.colors(
            containerColor = palette.surface,
            titleContentColor = palette.textSecondary,
            headlineContentColor = palette.textPrimary,
            weekdayContentColor = palette.textSecondary,
            subheadContentColor = palette.textSecondary,
            navigationContentColor = palette.textPrimary,
            yearContentColor = palette.textPrimary,
            currentYearContentColor = palette.accent,
            selectedYearContainerColor = palette.accent,
            selectedYearContentColor = palette.onAccent,
            dayContentColor = palette.textPrimary,
            disabledDayContentColor = palette.textSecondary.copy(alpha = 0.4f),
            selectedDayContainerColor = palette.accent,
            selectedDayContentColor = palette.onAccent,
            todayContentColor = palette.accent,
            todayDateBorderColor = palette.accent,
            dividerColor = palette.border,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = pickerColors,
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
                        fontWeight = FontWeight.SemiBold,
                        color = palette.accent
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(
                        text = "Cancelar",
                        color = palette.textSecondary
                    )
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = pickerColors
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

// (Se omitió aquí el bloque comentado de la versión anterior de CreateRouteScreen;
//  si lo necesitas como referencia, conserva el que ya tienes en tu archivo original.)

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
// Componentes de UI compartidos dentro de esta pantalla
// ---------------------------------------------------------------------------

@Composable
private fun SectionLabel(text: String) {
    val palette = rememberPalette()
    Text(
        text = text,
        style = RoutePlannerTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        color = palette.textSecondary,
    )
}

// Fila seleccionable: superficie con borde fino e ícono en acento.
@Composable
private fun SelectorRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isPlaceholder: Boolean = false,
    isLoading: Boolean = false,
) {
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = RoutePlannerTheme.dimens.spaceXxl)
            .clip(shape)
            .background(palette.surface)
            .border(1.dp, palette.border, shape)
            .clickable { onClick() }
            .padding(horizontal = RoutePlannerTheme.dimens.spaceMd),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp,
                color = palette.accent
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = palette.accent,
                modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm),
            )
        }
        Text(
            text = text,
            style = RoutePlannerTheme.typography.bodyMedium,
            color = if (isPlaceholder) palette.textSecondary else palette.textPrimary,
        )
    }
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
        placeholder = { Text(text = "Nombre de la ruta") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
        singleLine = true,
        colors = stopFieldColors(),
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
        SectionLabel(label)
        SelectorRow(
            icon = Icons.Default.LocationOn,
            text = selection,
            onClick = onSelectionChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionDialog(
    onDismiss: () -> Unit,
    onLocationSelected: () -> Unit,
    onFindDirections: () -> Unit
) {
    val palette = rememberPalette()
    ModalBottomSheet(
        containerColor = palette.surface,
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    tint = palette.accent,
                )
                Text(
                    text = "Usar ubicación actual",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = palette.textPrimary,
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
                    tint = palette.accent,
                )
                Text(
                    text = "Buscar direcciones",
                    style = RoutePlannerTheme.typography.bodyMedium,
                    color = palette.textPrimary,
                )
            }
        }
    }
}
package com.routeplanner.app.features.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.AddressSearchState
import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.SelectedAddress
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.Map
import com.swmansion.kmpmaps.core.Marker


enum class SelectionSource {
    SEARCH,
    MAP,
    IDLE
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchField(
    state: AddressSearchState,
    onAddressSelected: (AddressSuggestion, SelectedAddress) -> Unit,
    onValueChange: (String) -> Unit,
    clear: () -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    label: String = "Buscar dirección",
) {
    var selectedAddress by remember { mutableStateOf<AddressSuggestion?>(null) }
    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null,
        sheetGesturesEnabled = false,
        containerColor = RoutePlannerTheme.colors.primary
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(
                        horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                        vertical = RoutePlannerTheme.dimens.contentPaddingVertical
                    ),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXl)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Direcciones",
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
            OutlinedTextField(
                value = state.query,
                onValueChange = { onValueChange(it) },
                placeholder = {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    )
                },
                //label = { Text(label) },
                singleLine = true,
                trailingIcon = {
                    when {
                        state.isLoading -> CircularProgressIndicator(
                            Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )

                        state.query.isNotEmpty() -> IconButton(onClick = clear) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = RoutePlannerTheme.colors.primary.copy(alpha = 0.5f),
                    focusedContainerColor = RoutePlannerTheme.colors.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = RoutePlannerTheme.colors.secondary.copy(alpha = 0.6f),
                    focusedBorderColor = RoutePlannerTheme.colors.secondary,
                    cursorColor = RoutePlannerTheme.colors.onPrimary,
                ),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = state.suggestions.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    items(state.suggestions, key = { it.placeId }) { suggestion ->
                        SuggestionRow(
                            suggestion = suggestion,
                            isSelected = suggestion == selectedAddress,
                            onClick = {
                                selectedAddress = suggestion
                            }
                        )
                        HorizontalDivider(
                            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    selectedAddress?.let {
                        onSuggestionSelected(it) { address ->
                            onAddressSelected(
                                it,
                                address
                            )
                        }
                    }
                    onDismiss()
                },
                enabled = selectedAddress != null,
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
                    text = "Confirmar dirección",
                    style = RoutePlannerTheme.typography.titleMedium,
                    color = RoutePlannerTheme.colors.onSecondary,
                )
            }

            state.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressSearchField2(
    state: AddressSearchState,
    latitude: Double,
    longitude: Double,
    onAddressSelected: (AddressSuggestion, SelectedAddress) -> Unit,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
    onReverseGeocoding: (Double, Double) -> Unit,
    onConfirmAction: () -> Unit,
    onBack: () -> Unit = {},
    label: String = "Buscar dirección",
) {
    var selectedAddress by remember { mutableStateOf<AddressSuggestion?>(null) }

    var source by remember { mutableStateOf(SelectionSource.MAP) }
    var selectedCoordinates by remember(latitude, longitude) {
        mutableStateOf(
            Coordinates(
                latitude = latitude,
                longitude = longitude
            )
        )
    }
    var zoom by remember { mutableStateOf(16f) }

    val cameraPosition by remember(selectedCoordinates, zoom) {
        derivedStateOf {
            CameraPosition(
                coordinates = selectedCoordinates,
                zoom = zoom
            )
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(RoutePlannerTheme.colors.primary)
                .navigationBarsPadding(),
        //verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
    ) {

        Map(
            modifier = Modifier.fillMaxSize(),
            cameraPosition = cameraPosition,
            onCameraMove = { pos ->
                when (source) {
                    SelectionSource.SEARCH -> {
                        //selectedCoordinates = Coordinates(latitude, longitude)
                        source = SelectionSource.IDLE
                    }

                    else -> {
                        source = SelectionSource.MAP
                        pos.coordinates?.let {
                            selectedCoordinates = it
                        }
                        pos.zoom?.let {
                            zoom = it
                        }
                    }
                }
            },
            /*markers = listOf(
                Marker(
                    coordinates = selectedCoordinates,
                    title = null,
                    contentId = "marker"
                )
            ),
            customMarkerContent = mapOf(
                "marker" to {
                    Box {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación seleccionada",
                            tint = Color.Red,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(40.dp)
                        )
                    }
                }
            )*/
        )

        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Ubicación seleccionada",
            tint = Color.Red,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-24).dp)
                .size(48.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(all = RoutePlannerTheme.dimens.spaceMd)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Volver",
                        tint = RoutePlannerTheme.colors.onPrimary,
                    )
                }

                TextField(
                    value = state.query,
                    onValueChange = { onValueChange(it) },
                    placeholder = {
                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                        )
                    },
                    //label = { Text(label) },
                    singleLine = true,
                    trailingIcon = {
                        when {
                            state.isLoading -> CircularProgressIndicator(
                                Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )

                            state.query.isNotEmpty() -> IconButton(onClick = onClear) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        focusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = RoutePlannerTheme.colors.onPrimary,
                    ),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.weight(1f)
                )
            }

            if (state.suggestions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .height(280.dp)
                        .background(
                            color = RoutePlannerTheme.colors.primary,
                            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
                        )
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                    ) {
                        items(state.suggestions, key = { it.placeId }) { suggestion ->
                            SuggestionRow(
                                suggestion = suggestion,
                                isSelected = suggestion == selectedAddress,
                                onClick = {
                                    source = SelectionSource.SEARCH
                                    selectedAddress = suggestion
                                    selectedAddress?.let {
                                        onSuggestionSelected(it) { address ->
                                            onAddressSelected(
                                                it,
                                                address
                                            )
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(
                                color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceMd)
                )
                .navigationBarsPadding()
                .padding(vertical = RoutePlannerTheme.dimens.spaceMd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Mostrar coordenadas
            /*Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Latitud: ${selectedCoordinates.latitude}")
                Text("Longitud: ${selectedCoordinates.longitude}")

                Button(
                    onClick = {
                        // Confirmar ubicación seleccionada
                    }
                ) {
                    Text("Confirmar ubicación")
                }
            }*/

            Button(
                onClick = {
                    when (source) {
                        SelectionSource.MAP -> {
                            onReverseGeocoding(
                                selectedCoordinates.latitude,
                                selectedCoordinates.longitude
                            )
                        }

                        else -> {
                            /*selectedAddress?.let {
                                onSuggestionSelected(it) { address ->
                                    onAddressSelected(
                                        it,
                                        address
                                    )
                                }
                            }*/
                        }
                    }
                    onConfirmAction()
                },
                //enabled = selectedAddress != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoutePlannerTheme.colors.secondary,
                ),
            ) {
                Text(
                    text = "Confirmar dirección",
                    style = RoutePlannerTheme.typography.titleMedium,
                    color = RoutePlannerTheme.colors.onSecondary,
                )
            }
        }
        /*state.error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }*/
    }
}

@Composable
private fun SuggestionRow(
    suggestion: AddressSuggestion,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
            /*.background(
                if (isSelected)
                    RoutePlannerTheme.colors.primaryContainer.copy(alpha = 0.5f)
                else
                    RoutePlannerTheme.colors.primary
            )*/
            .clickable(onClick = onClick)
            .padding(
                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.padding(end = RoutePlannerTheme.dimens.spaceSm),
            tint = RoutePlannerTheme.colors.onPrimary
        )
        Column {
            Text(
                text = suggestion.primaryText,
                style = RoutePlannerTheme.typography.bodyMedium,
                color = RoutePlannerTheme.colors.onPrimary
            )
            suggestion.secondaryText?.let {
                Text(
                    text = it,
                    style = RoutePlannerTheme.typography.bodySmall,
                    color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.7f)
                )
            }
        }
        /*if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(end = RoutePlannerTheme.dimens.spaceSm),
                tint = Color.Green
            )
        }*/
    }
}
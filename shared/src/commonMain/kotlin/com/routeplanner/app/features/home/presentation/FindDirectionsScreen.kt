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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.AddressSearchState
import com.routeplanner.app.features.home.places.AddressSuggestion
import com.routeplanner.app.features.home.places.SelectedAddress

/*@Composable
fun AddressSearchField(
    state: AddressSearchState,
    onAddressSelected: (SelectedAddress) -> Unit,
    onValueChange: (String) -> Unit,
    clear: () -> Unit,
    onSuggestionSelected: (AddressSuggestion, (SelectedAddress) -> Unit) -> Unit,
    label: String = "Buscar dirección",
) {
    Column(modifier =
        Modifier
            .fillMaxSize()
            .safeContentPadding()
            .padding(
                horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal,
                vertical = RoutePlannerTheme.dimens.contentPaddingVertical
            )
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onValueChange(it) },
            label = { Text(label) },
            singleLine = true,
            trailingIcon = {
                when {
                    state.isLoading -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                    state.query.isNotEmpty() -> IconButton(onClick = clear) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(visible = state.suggestions.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)) {
                items(state.suggestions, key = { it.placeId }) { suggestion ->
                    SuggestionRow(
                        suggestion = suggestion,
                        onClick = { onSuggestionSelected(suggestion, onAddressSelected) }
                    )
                    HorizontalDivider()
                }
            }
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}*/

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
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(modifier =
            Modifier
                .fillMaxSize()
                .background(RoutePlannerTheme.colors.primary)
                .safeContentPadding()
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
                        state.isLoading -> CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
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
                    selectedAddress?.let { onSuggestionSelected(it) { address -> onAddressSelected(it, address) } }
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
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun SuggestionRow(
    suggestion: AddressSuggestion,
    isSelected: Boolean,
    onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd))
            .background(
                if (isSelected)
                    RoutePlannerTheme.colors.primaryContainer.copy(alpha = 0.5f)
                else
                    RoutePlannerTheme.colors.primary
            )
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
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.padding(end = RoutePlannerTheme.dimens.spaceSm),
                tint = Color.Green
            )
        }
    }
}
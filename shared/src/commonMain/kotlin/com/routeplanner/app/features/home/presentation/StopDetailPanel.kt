package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.StopState
import com.routeplanner.app.features.home.domain.model.StopStateEnum

@Composable
fun StopDetailPanel(
    state: StopDetailState,
    stopStates: List<StopState>,
    onStateChange: (StopState) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onMarkExitosa: () -> Unit,
    onMarkFallida: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val stop = state.stop ?: return
    val currentState = stopStates.find { it.description == stop.state }
        ?: StopState(1, "PENDIENTE")
    val shortAddress = stop.direction
        .split(",")
        .firstOrNull()
        ?.trim() ?: stop.direction

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = RoutePlannerTheme.colors.onPrimary
                )
            }
            Text(
                text = shortAddress,
                style = RoutePlannerTheme.typography.titleMedium,
                color = RoutePlannerTheme.colors.onPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        HorizontalDivider(color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.1f))

        // Info
        StopInfoRow(label = "Destinatario", value = stop.recipient)
        StopInfoRow(label = "Carácter", value = stop.notice)
        StopInfoRow(label = "Estado", value = stop.state)

        StopStateDropdown(
            current = currentState,
            states = stopStates,
            onSelected = { state ->
                onStateChange(state)
                onDismiss()
            }
        )

        // Nota
        OutlinedTextField(
            value = state.noteInput,
            onValueChange = onNoteChange,
            label = { Text("Nota") },
            minLines = 3,
            maxLines = 3,
            colors = stopFieldColors(),
            trailingIcon = {
                if (state.noteInput != (stop.note ?: "")) {
                    IconButton(onClick = onSaveNote) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = RoutePlannerTheme.colors.secondary
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Acciones — solo si está pendiente
        /*if (stop.state == StopStateEnum.PENDIENTE.description) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onMarkExitosa,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF34A853)
                    )
                ) {
                    Text("Exitosa", color = Color.White)
                }
                Button(
                    onClick = onMarkFallida,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Fallida", color = Color.White)
                }
            }
        }*/
    }
}

@Composable
private fun StopInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = RoutePlannerTheme.typography.bodyMedium,
            color = RoutePlannerTheme.colors.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopStateDropdown(
    current: StopState,
    states: List<StopState>,
    onSelected: (StopState) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = current.description,
            onValueChange = {},
            readOnly = true,
            label = { Text("Estado") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = stopFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            states.filter { it.id != 1 }.forEach { state ->
                DropdownMenuItem(
                    text = { Text(state.description) },
                    onClick = {
                        onSelected(state)
                        expanded = false
                    },
                    leadingIcon = {
                        if (state.id == current.id) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = RoutePlannerTheme.colors.secondary,
                                modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm)
                            )
                        }
                    }
                )
            }
        }
    }
}
package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.features.home.domain.model.Notices
import com.routeplanner.app.features.home.domain.model.StopState

@Composable
fun StopDetailPanel(
    state: StopDetailState,
    stopStates: List<StopState>,
    notices: List<Notices>,
    onStateChange: (StopState) -> Unit,
    onNoteChange: (String) -> Unit,
    onSaveNote: () -> Unit,
    onMarkExitosa: () -> Unit,
    onMarkFallida: () -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val stop = state.stop ?: return
    val palette = rememberPalette()
    val fieldShape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)

    var showConfirmDeleteStop by remember { mutableStateOf(false) }
    var stopToDeleteId by remember { mutableStateOf<String?>(null) }

    val currentState = stopStates.find { it.id == stop.stateId }
        ?: StopState(10, "No Notificado")

    val shortAddress = stop.direction
        .split(",")
        .firstOrNull()
        ?.trim() ?: stop.direction

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = palette.textPrimary
                )
            }
            Text(
                text = "Detalles de entrega",
                style = RoutePlannerTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = palette.textPrimary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = {
                showConfirmDeleteStop = true
                stopToDeleteId = state.stop.id
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = palette.danger
                )
            }
        }

        //HorizontalDivider(color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.1f))

        // Info
        //StopInfoRow(label = "Destinatario", value = stop.recipient)
        //StopInfoRow(label = "Carácter", value = notices.find { it.id == stop.noticeId }?.description
        //   ?: "")
        //StopInfoRow(label = "Estado", value = currentState.description)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = fieldShape,
            colors = CardDefaults.cardColors(containerColor = palette.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, palette.border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        all = RoutePlannerTheme.dimens.spaceMd
                    ),
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceXs),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = shortAddress,
                    style = RoutePlannerTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = palette.textPrimary
                )
                Text(
                    text = "${stop.recipient} · ${notices.find { it.id == stop.noticeId }?.description}",
                    style = RoutePlannerTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    color = palette.textSecondary
                )
            }
        }

        StopStateDropdown(
            current = currentState,
            states = stopStates,
            palette = palette,
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
            shape = fieldShape,
            colors = stopFieldColors(palette),
            trailingIcon = {
                if (state.noteInput != (stop.note ?: "")) {
                    IconButton(onClick = onSaveNote) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Guardar",
                            tint = palette.accent
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
        if (showConfirmDeleteStop) {
            ConfirmDeleteDialog(
                text = "¿Eliminar entrega?",
                onDismiss = { showConfirmDeleteStop = false },
                onDelete = {
                    showConfirmDeleteStop = false
                    stopToDeleteId?.let {
                        onDelete(it)
                        stopToDeleteId = null
                    }
                }
            )
        }
    }
}

@Composable
private fun StopInfoRow(label: String, value: String) {
    val palette = rememberPalette()
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = palette.textSecondary
        )
        Text(
            text = value,
            style = RoutePlannerTheme.typography.bodyMedium,
            color = palette.textPrimary
        )
    }
}

// Campos de texto: contenedor de superficie, borde fino y acento al enfocar.
@Composable
private fun stopFieldColors(palette: UiPalette): TextFieldColors =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = palette.textPrimary,
        unfocusedTextColor = palette.textPrimary,
        focusedContainerColor = palette.surface,
        unfocusedContainerColor = palette.surface,
        focusedBorderColor = palette.accent,
        unfocusedBorderColor = palette.border,
        focusedLabelColor = palette.accent,
        unfocusedLabelColor = palette.textSecondary,
        focusedTrailingIconColor = palette.textSecondary,
        unfocusedTrailingIconColor = palette.textSecondary,
        cursorColor = palette.accent,
        selectionColors = androidx.compose.foundation.text.selection.TextSelectionColors(
            handleColor = palette.accent,
            backgroundColor = palette.accent.copy(alpha = 0.25f)
        ),
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopStateDropdown(
    current: StopState,
    states: List<StopState>,
    palette: UiPalette,
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
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
            colors = stopFieldColors(palette),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = palette.surface,
            border = BorderStroke(1.dp, palette.border),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
        ) {
            states.forEach { state ->
                val selected = state.id == current.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = state.description,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (selected) palette.accent else palette.textPrimary,
                    ),
                    onClick = {
                        onSelected(state)
                        expanded = false
                    },
                    leadingIcon = {
                        if (selected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = palette.accent,
                                modifier = Modifier.size(RoutePlannerTheme.dimens.iconSizeSm)
                            )
                        }
                    }
                )
            }
        }
    }
}
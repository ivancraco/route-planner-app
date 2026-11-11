package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.core.ui.rememberPalette
import com.routeplanner.app.features.home.domain.model.Notices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopFormDialog(
    state: StopFormState,
    notices: List<Notices>,
    onRecipientChange: (String) -> Unit,
    onDirectionDetailChange: (String) -> Unit,
    onNoticeChange: (Notices) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onConfirmAndOther: () -> Unit,
    onDismiss: () -> Unit,
    onBack: () -> Unit
) {
    val palette = rememberPalette()
    val shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd)
    val canSave = state.recipient.isNotBlank() && !state.isLoading && state.notice != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = palette.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.background,
                    scrolledContainerColor = palette.background,
                ),
                title = {
                    Text(
                        text = "Datos de entrega",
                        style = RoutePlannerTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.textPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Volver",
                            tint = palette.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(palette.background)
                .padding(all = RoutePlannerTheme.dimens.spaceMd),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceLg)
        ) {
            val shortAddress = state.direction.split(",")
                .firstOrNull()
                ?.trim() ?: state.direction

            // Ubicación confirmada
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                    horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(palette.accentSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            "",
                            tint = palette.accent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = shortAddress,
                            style = RoutePlannerTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = palette.textPrimary
                        )
                        Text(
                            text = "Ubicación confirmada",
                            style = RoutePlannerTheme.typography.bodySmall,
                            color = palette.textSecondary
                        )
                    }
                }
            }

            // Destinatario
            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                FieldLabel("Destinatario")
                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = onRecipientChange,
                    singleLine = true,
                    colors = stopFieldColors(),
                    shape = shape,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Piso / Depto
            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                FieldLabel("Piso / Depto")
                OutlinedTextField(
                    value = state.directionDetail,
                    onValueChange = { value ->
                        if (value.trim().length <= 30)
                            onDirectionDetailChange(value)
                    },
                    singleLine = true,
                    colors = stopFieldColors(),
                    shape = shape,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Carácter
            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                FieldLabel("Carácter")
                StopNoticeDropdown(
                    notices = notices,
                    selected = state.notice,
                    onSelected = onNoticeChange
                )
            }

            // Referencia
            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                FieldLabel("Referencia (opcional)")
                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    //singleLine = true,
                    minLines = 2,
                    maxLines = 2,
                    colors = stopFieldColors(),
                    shape = shape,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // Nota opcional
            /*OutlinedTextField(
                value = state.note,
                onValueChange = onNoteChange,
                label = { Text("Nota (opcional)") },
                minLines = 2,
                maxLines = 2,
                colors = stopFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )*/

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
            ) {
                // Secundaria
                OutlinedButton(
                    onClick = onConfirmAndOther,
                    enabled = canSave,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = shape,
                    border = BorderStroke(
                        1.dp,
                        if (canSave) palette.accent else palette.border
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = palette.accent,
                        disabledContentColor = palette.textSecondary.copy(alpha = 0.5f),
                    )
                ) {
                    Text(
                        text = "Guardar y otra",
                        fontWeight = FontWeight.Medium,
                    )
                }
                // Principal
                Button(
                    onClick = onConfirm,
                    enabled = canSave,
                    modifier = Modifier
                        .weight(1f)
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
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = palette.onAccent
                        )
                    } else {
                        Text(
                            text = "Guardar",
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    val palette = rememberPalette()
    Text(
        text = text,
        style = RoutePlannerTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        color = palette.textSecondary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopNoticeDropdown(
    notices: List<Notices>,
    selected: Notices?,
    onSelected: (Notices) -> Unit
) {
    val palette = rememberPalette()
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.description ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Seleccionar carácter") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
            colors = stopFieldColors(),
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
            notices.forEach { notice ->
                val isSelected = selected?.id == notice.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = notice.description,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = if (isSelected) palette.accent else palette.textPrimary,
                    ),
                    onClick = {
                        onSelected(notice)
                        expanded = false
                    },
                    leadingIcon = {
                        if (isSelected) {
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

// Campos de texto: superficie, borde fino y acento al enfocar.
// Mantiene la misma firma pública, así que otras pantallas que la usen se actualizan solas.
@Composable
fun stopFieldColors() : androidx.compose.material3.TextFieldColors {
    val palette = rememberPalette()
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = palette.textPrimary,
        unfocusedTextColor = palette.textPrimary,
        focusedContainerColor = palette.surface,
        unfocusedContainerColor = palette.surface,
        focusedBorderColor = palette.accent,
        unfocusedBorderColor = palette.border,
        focusedLabelColor = palette.accent,
        unfocusedLabelColor = palette.textSecondary,
        focusedPlaceholderColor = palette.textSecondary,
        unfocusedPlaceholderColor = palette.textSecondary,
        focusedTrailingIconColor = palette.textSecondary,
        unfocusedTrailingIconColor = palette.textSecondary,
        cursorColor = palette.accent,
        selectionColors = TextSelectionColors(
            handleColor = palette.accent,
            backgroundColor = palette.accent.copy(alpha = 0.25f)
        ),
    )
}
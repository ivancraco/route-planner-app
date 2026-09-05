package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum

@Composable
fun StopFormDialog(
    state: StopFormState,
    onRecipientChange: (String) -> Unit,
    onNoticeChange: (StopNoticeEnum) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = RoutePlannerTheme.colors.primaryContainer,
            tonalElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Nuevo punto de entrega",
                    style = RoutePlannerTheme.typography.titleLarge,
                    color = RoutePlannerTheme.colors.onPrimary
                )

                Text(
                    text = state.direction,
                    style = RoutePlannerTheme.typography.bodySmall,
                    color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.6f)
                )

                // Destinatario
                OutlinedTextField(
                    value = state.recipient,
                    onValueChange = onRecipientChange,
                    label = { Text("Destinatario") },
                    singleLine = true,
                    colors = stopFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Carácter
                StopNoticeDropdown(
                    selected = state.notice,
                    onSelected = onNoticeChange
                )

                // Nota opcional
                OutlinedTextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    label = { Text("Nota (opcional)") },
                    maxLines = 3,
                    colors = stopFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onConfirm,
                    enabled = state.recipient.isNotBlank() && !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Crear punto de entrega",
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopNoticeDropdown(
    selected: StopNoticeEnum,
    onSelected: (StopNoticeEnum) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.description,
            onValueChange = {},
            readOnly = true,
            label = { Text("Carácter") },
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
            StopNoticeEnum.entries.forEach { notice ->
                DropdownMenuItem(
                    text = { Text(notice.description) },
                    onClick = {
                        onSelected(notice)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun stopFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = RoutePlannerTheme.colors.onPrimary,
    unfocusedTextColor = RoutePlannerTheme.colors.onPrimary,
    focusedLabelColor = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.7f),
    unfocusedLabelColor = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.5f),
    focusedBorderColor = RoutePlannerTheme.colors.secondary,
    unfocusedBorderColor = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.3f),
    cursorColor = RoutePlannerTheme.colors.onPrimary,
)
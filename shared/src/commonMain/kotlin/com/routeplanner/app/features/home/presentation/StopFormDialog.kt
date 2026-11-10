package com.routeplanner.app.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.domain.model.Notices
import com.routeplanner.app.features.home.domain.model.StopNoticeEnum

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
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Datos de entrega",
                        style = RoutePlannerTheme.typography.titleLarge,
                        color = RoutePlannerTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            "Volver",
                            tint = RoutePlannerTheme.colors.onPrimary
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
                .background(RoutePlannerTheme.colors.primary)
                .padding(all = RoutePlannerTheme.dimens.spaceMd),
            verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceLg)
        ) {
            val shortAddress = state.direction.split(",")
                .firstOrNull()
                ?.trim() ?: state.direction

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = RoutePlannerTheme.dimens.spaceMd),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceMd)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        "",
                        tint = RoutePlannerTheme.colors.secondary
                    )
                    Column {
                        Text(
                            text = shortAddress,
                            style = RoutePlannerTheme.typography.titleMedium,
                            color = RoutePlannerTheme.colors.onPrimary
                        )
                        Text(
                            text = "Ubicación confirmada",
                            style = RoutePlannerTheme.typography.bodySmall,
                            color = RoutePlannerTheme.colors.onPrimary.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                Text(
                    text = "Destinatario",
                    style = RoutePlannerTheme.typography.titleMedium
                )
                // Destinatario
                TextField(
                    value = state.recipient,
                    onValueChange = onRecipientChange,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = RoutePlannerTheme.colors.secondary,
                        unfocusedIndicatorColor = RoutePlannerTheme.colors.primaryContainer,
                        focusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        unfocusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        cursorColor = RoutePlannerTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceSm),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                Text(
                    text = "Piso / Depto",
                    style = RoutePlannerTheme.typography.titleMedium
                )
                TextField(
                    value = state.directionDetail,
                    onValueChange = { value ->
                        if (value.trim().length <= 30)
                            onDirectionDetailChange(value)
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = RoutePlannerTheme.colors.secondary,
                        unfocusedIndicatorColor = RoutePlannerTheme.colors.primaryContainer,
                        focusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        unfocusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        cursorColor = RoutePlannerTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceSm),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Carácter
            StopNoticeDropdown(
                notices = notices,
                selected = state.notice,
                onSelected = onNoticeChange
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(RoutePlannerTheme.dimens.spaceSm)
            ) {
                Text(
                    text = "Referencia (opcional)",
                    style = RoutePlannerTheme.typography.titleMedium
                )
                TextField(
                    value = state.note,
                    onValueChange = onNoteChange,
                    //singleLine = true,
                    minLines = 2,
                    maxLines = 2,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = RoutePlannerTheme.colors.secondary,
                        unfocusedIndicatorColor = RoutePlannerTheme.colors.primaryContainer,
                        focusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        unfocusedContainerColor = RoutePlannerTheme.colors.primaryContainer,
                        cursorColor = RoutePlannerTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.spaceSm),
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
                Button(
                    onClick = onConfirmAndOther,
                    enabled = state.recipient.isNotBlank() && !state.isLoading && state.notice != null,
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Guardar y otra",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = onConfirm,
                    enabled = state.recipient.isNotBlank() && !state.isLoading && state.notice != null,
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Text(
                            text = "Guardar",
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
    notices: List<Notices>,
    selected: Notices?,
    onSelected: (Notices) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected?.description ?: "Seleccionar Carácter",
            onValueChange = {},
            readOnly = true,
            label = { Text("Carácter") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = stopFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            notices.forEach { notice ->
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
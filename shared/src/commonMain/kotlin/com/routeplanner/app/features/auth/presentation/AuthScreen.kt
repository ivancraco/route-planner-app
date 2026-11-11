package com.routeplanner.app.features.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.routeplanner.app.core.ui.RoutePlannerTheme
import com.routeplanner.app.features.home.presentation.stopFieldColors
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import route_planner_app.shared.generated.resources.Res
import route_planner_app.shared.generated.resources.app_icon

@Composable
fun AuthScreen(
    onNavigateAfterLogin: (isSupervisor: Boolean) -> Unit,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateAfterLogin -> onNavigateAfterLogin(effect.isSupervisor)
            }
        }
    }

    AuthContent(state = uiState, onEvent = viewModel::onEvent)
}

@Composable
private fun AuthContent(
    state: AuthUiState,
    onEvent: (AuthEvent) -> Unit
) {
    val palette = com.routeplanner.app.core.ui.rememberPalette()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(AuthEvent.ErrorMessageShown)
        }
    }

    Scaffold(
        containerColor = palette.background,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                // Snackbar invertido: contraste alto y sin color decorativo
                Snackbar(
                    snackbarData = data,
                    shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
                    containerColor = palette.textPrimary,
                    contentColor = palette.background,
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(palette.background)
                .padding(padding)
                .padding(
                    horizontal = RoutePlannerTheme.dimens.contentPaddingHorizontal
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(96.dp))
            Icon(
                painter = painterResource(Res.drawable.app_icon),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = "NotiRuta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = palette.textPrimary
            )

            Spacer(Modifier.height(56.dp))

            AuthTextField(
                value = state.username,
                onValueChange = { onEvent(AuthEvent.UsernameChanged(it)) },
                placeholder = "Usuario"
            )

            Spacer(Modifier.height(16.dp))

            AuthTextField(
                value = state.password,
                onValueChange = { onEvent(AuthEvent.PasswordChanged(it)) },
                placeholder = "Contraseña",
                isPassword = true
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = { onEvent(AuthEvent.LoginClicked) },
                enabled = state.isLoginEnabled,
                shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
                colors = ButtonDefaults.buttonColors(
                    containerColor = palette.accent,
                    contentColor = palette.onAccent,
                    disabledContainerColor = palette.border,
                    disabledContentColor = palette.textSecondary.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                modifier = Modifier.height(48.dp).fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = palette.onAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar sesión", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    val palette = com.routeplanner.app.core.ui.rememberPalette()
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = placeholder) },
        singleLine = true,
        textStyle = TextStyle(
            color = palette.textPrimary,
            fontSize = RoutePlannerTheme.typography.bodyMedium.fontSize,
            fontWeight = RoutePlannerTheme.typography.bodyMedium.fontWeight
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
        ),
        shape = RoundedCornerShape(RoutePlannerTheme.dimens.radiusMd),
        colors = stopFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}
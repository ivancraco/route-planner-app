package com.routeplanner.app.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * EJEMPLO de integración con Compose Navigation. Esto es solo referencia:
 * copiá el composable("auth") { ... } dentro de tu NavHost real, junto a tus
 * rutas de "home" (supervisor) y "home" (no supervisor).
 *
 * Los nombres de ruta ("auth", "home_supervisor", "home_operator") son
 * placeholders: reemplazalos por los que ya uses en tu app.
 */
object AuthDestinations {
    const val AUTH_ROUTE = "auth"
}


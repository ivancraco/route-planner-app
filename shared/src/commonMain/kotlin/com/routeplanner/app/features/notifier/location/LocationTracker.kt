package com.routeplanner.app.features.notifier.location

import androidx.compose.runtime.Composable

/**
 * Contrato común para obtener la ubicación actual del dispositivo.
 * Las implementaciones reales viven en androidMain (FusedLocationProviderClient)
 * y en iosMain (CLLocationManager).
 *
 * IMPORTANTE: solo debe llamarse a getCurrentLocation() después de que el
 * permiso Permission LOCATION de moko-permissions haya sido concedido.
 * Ver LocationCaptureController.kt para el flujo completo.
 */
interface LocationTracker {
    suspend fun getCurrentLocation(): LocationCoordinates
}

/**
 * Composable expect/actual: entrega la implementación de LocationTracker
 * adecuada según la plataforma (Android necesita Context, iOS no).
 */
@Composable
expect fun rememberLocationTracker(): LocationTracker
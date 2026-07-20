package com.routeplanner.app.features.home.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory

/**
 * Estados posibles del flujo de captura de ubicación.
 */
sealed interface LocationCaptureState {
    data object Idle : LocationCaptureState
    data object Loading : LocationCaptureState
    data class Success(val coordinates: LocationCoordinates) : LocationCaptureState
    data class Error(val message: String, val isPermanentlyDenied: Boolean = false) : LocationCaptureState
}

/**
 * Une la solicitud de permiso (moko-permissions) con la captura de coordenadas
 * (LocationTracker). Es independiente de la plataforma: usa las mismas APIs
 * comunes que ya tenés en tu BindEffect / PermissionsController actual.
 */
class LocationCaptureController(
    private val permission: Permission,
    private val permissionsController: PermissionsController,
    private val locationTracker: LocationTracker
) {

    /**
     * Pide el permiso de ubicación (si no fue concedido todavía) y luego
     * captura las coordenadas actuales. Devuelve un LocationCaptureState
     * listo para pintar en la UI.
     */
    suspend fun captureLocation(): LocationCaptureState {
        return try {
            permissionsController.providePermission(permission)
            val coordinates = locationTracker.getCurrentLocation()
            LocationCaptureState.Success(coordinates)
        } catch (e: DeniedAlwaysException) {
            LocationCaptureState.Error(
                message = "Denegaste el permiso de ubicación de forma permanente. " +
                        "Habilitalo desde los ajustes del sistema.",
                isPermanentlyDenied = true
            )
        } catch (e: DeniedException) {
            LocationCaptureState.Error("Permiso de ubicación denegado.")
        } catch (e: RequestCanceledException) {
            LocationCaptureState.Error("Se canceló la solicitud de permiso.")
        } catch (e: Exception) {
            LocationCaptureState.Error(e.message ?: "No se pudo obtener la ubicación.")
        }
    }

    /** Abre la pantalla de configuración de la app (Android e iOS). */
    fun openAppSettings() {
        permissionsController.openAppSettings()
    }
}

/**
 * Hook composable: crea (y mantiene vivo entre recomposiciones) el
 * LocationCaptureController, ya enlazado al lifecycle vía BindEffect.
 */
@Composable
fun rememberLocationCaptureController(permission: Permission): LocationCaptureController {
    val factory = rememberPermissionsControllerFactory()
    val permissionsController = remember(factory) { factory.createPermissionsController() }
    BindEffect(permissionsController)

    val locationTracker = rememberLocationTracker()

    return remember(permissionsController, locationTracker) {
        LocationCaptureController(permission, permissionsController, locationTracker)
    }
}
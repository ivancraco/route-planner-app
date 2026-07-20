package com.routeplanner.app.features.home.location

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementación Android. Requiere haber agregado:
 *   implementation("com.google.android.gms:play-services-location:21.3.0")
 * en el sourceSet androidMain, y los permisos en el AndroidManifest:
 *   <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 *   <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 */
class AndroidLocationTracker(
    private val context: Context
) : com.routeplanner.app.features.home.location.LocationTracker {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // El @SuppressLint es seguro porque getCurrentLocation() solo se invoca
    // luego de que moko-permissions confirmó que el permiso está concedido.
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): LocationCoordinates =
        suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(
                        LocationCoordinates(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    continuation.resumeWithException(
                        IllegalStateException(
                            "No se pudo obtener la ubicación. Verificá que el GPS esté activado."
                        )
                    )
                }
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
}

@Composable
actual fun rememberLocationTracker(): LocationTracker {
    val context = LocalContext.current
    return remember { AndroidLocationTracker(context) }
}
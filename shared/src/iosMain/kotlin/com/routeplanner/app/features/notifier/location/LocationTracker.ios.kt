package com.routeplanner.app.features.notifier.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementación iOS. Requiere agregar en Info.plist:
 *   NSLocationWhenInUseUsageDescription
 * (la solicitud del permiso en sí la maneja moko-permissions con Permission;
 * esta clase solo lee la coordenada una vez que el permiso ya está concedido).
 */
@OptIn(ExperimentalForeignApi::class)
class IosLocationTracker : LocationTracker {

    private val locationManager = CLLocationManager()

    override suspend fun getCurrentLocation(): LocationCoordinates =
        suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null && continuation.isActive) {
                        val coordinates = location.coordinate.useContents {
                            LocationCoordinates(latitude = latitude, longitude = longitude)
                        }
                        continuation.resume(coordinates)
                    }
                    manager.stopUpdatingLocation()
                }

                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            IllegalStateException(didFailWithError.localizedDescription)
                        )
                    }
                    manager.stopUpdatingLocation()
                }
            }

            locationManager.delegate = delegate
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
}

@Composable
actual fun rememberLocationTracker(): LocationTracker {
    return remember { IosLocationTracker() }
}
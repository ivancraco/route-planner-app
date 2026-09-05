package com.routeplanner.app.features.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.routeplanner.app.features.home.domain.model.UserRoute
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.location.LocationCoordinates
import com.swmansion.kmpmaps.core.AndroidMapProperties
import com.swmansion.kmpmaps.core.CameraPosition
import com.swmansion.kmpmaps.core.Coordinates
import com.swmansion.kmpmaps.core.GoogleMapsMapStyleOptions
import com.swmansion.kmpmaps.core.IosMapProperties
import com.swmansion.kmpmaps.core.Map
import com.swmansion.kmpmaps.core.MapProperties
import com.swmansion.kmpmaps.core.MapType
import com.swmansion.kmpmaps.core.MapUISettings
import com.swmansion.kmpmaps.core.Marker
import com.swmansion.kmpmaps.core.Polyline

// ---------------------------------------------------------------------------
// Construye el mapa de customMarkerContent a partir de la lista de paradas
//
// La API de kmp-maps espera:
//   customMarkerContent: Map<String, @Composable (Marker) -> Unit>
//
// Donde la clave es el contentId asignado al Marker.
// Cada lambda recibe el Marker (para poder leer sus datos si hace falta)
// y devuelve el Composable que se renderiza como ícono.
// ---------------------------------------------------------------------------

@Composable
fun buildCustomMarkerContent(
    stops: UserRoute?,
): kotlin.collections.Map<String, @Composable (Marker) -> Unit> {
    if (stops == null) return emptyMap()
    val content = mutableMapOf<String, @Composable (Marker) -> Unit>()

    content[MarkerContentId.ORIGIN] = { _ -> OriginMarker() }
    content[MarkerContentId.DESTINATION] = { _ -> DestinationMarker() }
    stops.userStops.forEach { stop ->
        //val waypointOrder = index  // 0-based; el origen no se numera
        val order = stop.order
        val contentId = MarkerContentId.waypoint(order)
        content[contentId] = { _ -> WaypointMarker(order = order) }
    }
    /*stops.forEachIndexed { index, stop ->
        val waypointOrder = index  // 0-based; el origen no se numera
        when {
            stop.isOrigin -> {
                content[MarkerContentId.ORIGIN] = { _ -> OriginMarker() }
            }

            stop.isDestination -> {
                content[MarkerContentId.DESTINATION] = { _ -> DestinationMarker() }
            }

            else -> {
                // Las paradas intermedias se numeran a partir de 1
                val order = stops
                    .take(index)
                    .count { !it.isOrigin && !it.isDestination } + 1
                val contentId = MarkerContentId.waypoint(order)
                content[contentId] = { _ -> WaypointMarker(order = order) }
            }
        }
    }*/

    return content
}

// ---------------------------------------------------------------------------
// Convierte RouteStop → Marker de kmp-maps con su contentId asignado
// ---------------------------------------------------------------------------
fun UserStop.toKmpMarker(waypointOrder: Int): Marker {
    val shortAddress = direction.split(",")
        .firstOrNull()
        ?.trim() ?: direction
    return Marker(
        coordinates = Coordinates(
            latitude = latitude,
            longitude = longitude,
        ),
        title = shortAddress,
        contentId = MarkerContentId.waypoint(waypointOrder)
    )
}

fun UserRoute.toKmpMarkers(): List<Marker> {
    val sortedStops = userStops.sortedBy { it.order }  // ← ordenar primero
    val stopMarkers = sortedStops.map { stop ->
        stop.toKmpMarker(waypointOrder = stop.order)
    }
    val result = mutableListOf<Marker>()
    result.add(
        Marker(
            coordinates = Coordinates(originLatitude, originLongitude),
            title = originDir,
            contentId = MarkerContentId.ORIGIN
        )
    )
    result.addAll(stopMarkers)
    return result

    /*var waypointCounter = 1
    val stops = userStops.map { stop ->
        waypointCounter++
        stop.toKmpMarker(waypointOrder = stop.order.toInt())
    }
    val result = mutableListOf<Marker>()
    result.add(
        Marker(
            coordinates = Coordinates(
                latitude = originLatitude,
                longitude = originLongitude,
            ),
            title = originDir,
            contentId = MarkerContentId.ORIGIN
        )
    )
    result.addAll(stops)
    result.add(
        Marker(
            coordinates = Coordinates(
                latitude = destinationLatitude,
                longitude = destinationLongitude,
            ),
            title = destinationDir,
            contentId = MarkerContentId.DESTINATION
        )
    )
    return result*/
}

// ---------------------------------------------------------------------------
// Composable del mapa completo — reemplaza el bloque Map() en RoutePlannerScreen
// ---------------------------------------------------------------------------

@Composable
fun RouteMap(
    modifier: Modifier = Modifier,
    userRoute: UserRoute?,
    routePolyline: List<Coordinates> = emptyList(),
    isMyLocationEnabled: Boolean = false,
    onMarkerClick: (UserStop) -> Unit = {},
    coordinates: LocationCoordinates?,
) {
    /*val initialCoords = stops.firstOrNull()?.coordinates
        ?: Coordinates(-34.6037, -58.3816)*/

    /*val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition(
            coordinates = Coordinates(
                latitude  = initialCoords.latitude,
                longitude = initialCoords.longitude,
            ),
            zoom = 12f,
        )
    }*/

    // Construir markers y su contenido custom
    val markers = userRoute?.toKmpMarkers()
    val customMarkerContent = buildCustomMarkerContent(userRoute)

    // Polyline de la ruta
    val polylines = if (routePolyline.size >= 2) {
        listOf(
            Polyline(
                coordinates = routePolyline,
                width = 8f,
                lineColor = Color(0xFF1A73E8),
            )
        )
    } else emptyList()

    Map(
        modifier = modifier,
        cameraPosition = CameraPosition(
            coordinates =
                Coordinates(
                    -31.39676517404372,
                    -58.01713884030075,
                ),
            /*if (coordinates != null) Coordinates(coordinates.latitude, longitude = coordinates.longitude)
            else Coordinates(latitude = -31.388881, longitude = -58.013577),*/
            zoom = 13.8f
        ),
        properties = MapProperties(
            isMyLocationEnabled = true,
            isTrafficEnabled = false,
            isBuildingEnabled = false,
            mapType = MapType.NORMAL,
            androidMapProperties = AndroidMapProperties(
                mapStyleOptions = GoogleMapsMapStyleOptions(
                    """
[
  { "featureType": "poi",            "stylers": [{ "visibility": "off" }] },
  { "featureType": "transit",        "stylers": [{ "visibility": "off" }] },
  { "featureType": "administrative", "stylers": [{ "visibility": "off" }] },
]
"""
                )
            ),
            iosMapProperties = IosMapProperties(
                showPOI = false,
            )
        ),
        uiSettings = MapUISettings(
            myLocationButtonEnabled = false,
            compassEnabled = false,
            zoomEnabled = true,
            scrollEnabled = true,
            scaleBarEnabled = false,
            togglePitchEnabled = false
        ),
        markers = markers ?: emptyList(),
        polylines = polylines,
        customMarkerContent = customMarkerContent,
        onMarkerClick = { marker ->
            val stop = userRoute?.userStops?.firstOrNull { it.direction == marker.title }
            //val stop = notifierRoute { it.label == marker.title }
            stop?.let { onMarkerClick(it) }
        },
    )
}
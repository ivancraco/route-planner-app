package com.routeplanner.app.features.home.data.remote.api

import com.routeplanner.app.features.home.domain.model.TravelMode
import com.routeplanner.app.features.home.domain.model.UserStop
import com.routeplanner.app.features.home.places.LatLngDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class RoutesApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    suspend fun computeOptimalRoute(
        originLatitude: Double,
        originLongitude: Double,
        stops: List<UserStop>,
        travelMode: TravelMode
    ): Result<OptimalRouteResult> = runCatching {

        val isWalk = travelMode == TravelMode.WALK
        val farthest = if (isWalk && stops.isNotEmpty()) {
            farthestStopIndex(originLatitude, originLongitude, stops)
        } else {
            null
        }

        // WALK: ida sola — el destino es la parada más lejana, las demás son intermedias.
        // DRIVE: ida y vuelta al origen (sin cambios).
        val intermediateIndices = if (farthest != null) {
            stops.indices.filter { it != farthest }
        } else {
            stops.indices.toList()
        }

        val waypoints = intermediateIndices.map { i ->
            RouteWaypoint(
                location = RouteLocation(
                    latLng = LatLngDto(stops[i].latitude, stops[i].longitude)
                )
            )
        }

        val destinationLatitude = if (farthest != null) stops[farthest].latitude else originLatitude
        val destinationLongitude = if (farthest != null) stops[farthest].longitude else originLongitude

        val response = httpClient.post(
            "https://routes.googleapis.com/directions/v2:computeRoutes"
        ) {
            contentType(ContentType.Application.Json)
            header("X-Goog-Api-Key", apiKey)
            header(
                "X-Goog-FieldMask",
                "routes.optimizedIntermediateWaypointIndex,routes.polyline.encodedPolyline,routes.legs"
            )
            setBody(
                ComputeRoutesRequest(
                    origin = RouteWaypoint(
                        location = RouteLocation(
                            latLng = LatLngDto(originLatitude, originLongitude)
                        )
                    ),
                    destination = RouteWaypoint(
                        location = RouteLocation(
                            latLng = LatLngDto(destinationLatitude, destinationLongitude)
                        )
                    ),
                    intermediates = waypoints,
                    optimizeWaypointOrder = true,
                    travelMode = travelMode.googleValue,
                )
            )
        }.body<ComputeRoutesResponse>()

        val route = response.routes.firstOrNull()
            ?: throw Exception("No se obtuvo ruta")

        val orderedIndices = resolveVisitOrder(
            travelMode = travelMode,
            stops = stops,
            originLatitude = originLatitude,
            originLongitude = originLongitude,
            optimizedIntermediateWaypointIndex = route.optimizedIntermediateWaypointIndex,
        )

        OptimalRouteResult(
            orderedStopIndices = orderedIndices,
            encodedPolyline = route.polyline?.encodedPolyline ?: ""
        )
    }
}

// ─── Request ──────────────────────────────────────────────────

@Serializable
data class ComputeRoutesRequest(
    val origin: RouteWaypoint,
    val destination: RouteWaypoint,
    val intermediates: List<RouteWaypoint>,
    val optimizeWaypointOrder: Boolean,
    val travelMode: String,
)

@Serializable
data class RouteWaypoint(
    val location: RouteLocation
)

@Serializable
data class RouteLocation(
    val latLng: LatLngDto
)

// ─── Response ─────────────────────────────────────────────────

@Serializable
data class ComputeRoutesResponse(
    val routes: List<ComputedRoute> = emptyList()
)

@Serializable
data class ComputedRoute(
    val optimizedIntermediateWaypointIndex: List<Int>? = null,
    val polyline: EncodedPolyline? = null,
)

@Serializable
data class EncodedPolyline(
    val encodedPolyline: String
)

// ─── Resultado ────────────────────────────────────────────────

data class OptimalRouteResult(
    val orderedStopIndices: List<Int>,
    val encodedPolyline: String
)

// ─── Orden de visita (índices sobre la lista original de paradas) ────

internal fun farthestStopIndex(
    originLatitude: Double,
    originLongitude: Double,
    stops: List<UserStop>,
): Int = stops.indices.maxByOrNull { i ->
    haversineMeters(originLatitude, originLongitude, stops[i].latitude, stops[i].longitude)
} ?: 0

internal fun resolveVisitOrder(
    travelMode: TravelMode,
    stops: List<UserStop>,
    originLatitude: Double,
    originLongitude: Double,
    optimizedIntermediateWaypointIndex: List<Int>?,
): List<Int> {
    if (travelMode == TravelMode.DRIVE) {
        return optimizedIntermediateWaypointIndex ?: stops.indices.toList()
    }
    if (stops.isEmpty()) return emptyList()

    val farthest = farthestStopIndex(originLatitude, originLongitude, stops)
    val intermediates = stops.indices.filter { it != farthest }
    val optimized = optimizedIntermediateWaypointIndex ?: intermediates.indices.toList()
    return optimized.map { intermediates[it] } + farthest
}

private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6_371_000.0
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2) * sin(dLon / 2)
    return 2 * earthRadius * asin(sqrt(a))
}
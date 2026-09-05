package com.routeplanner.app.features.home.data.remote.api

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

class RoutesApi(
    private val httpClient: HttpClient,
    private val apiKey: String
) {
    suspend fun computeOptimalRoute(
        originLatitude: Double,
        originLongitude: Double,
        stops: List<UserStop>
    ): Result<OptimalRouteResult> = runCatching {

        val waypoints = stops.map {
            RouteWaypoint(
                location = RouteLocation(
                    latLng = LatLngDto(it.latitude, it.longitude)
                )
            )
        }

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
                            latLng = LatLngDto(originLatitude, originLongitude) // vuelve al origen
                        )
                    ),
                    intermediates = waypoints,
                    optimizeWaypointOrder = true,
                    travelMode = "DRIVE",
                )
            )
        }.body<ComputeRoutesResponse>()

        val route = response.routes.firstOrNull()
            ?: throw Exception("No se obtuvo ruta")
        val indices = route.optimizedIntermediateWaypointIndex
            ?: (stops.indices.toList())

        OptimalRouteResult(
            orderedStopIndices = indices,
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
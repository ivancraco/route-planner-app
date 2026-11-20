package com.routeplanner.app

import com.routeplanner.app.features.home.data.remote.api.resolveVisitOrder
import com.routeplanner.app.features.home.domain.model.TravelMode
import com.routeplanner.app.features.home.domain.model.UserStop
import kotlin.test.Test
import kotlin.test.assertEquals

class RouteOptimizationTest {

    private fun stop(id: String, lat: Double, lng: Double) = UserStop(
        id = id,
        routeId = "r",
        noticeId = 1,
        stateId = 1,
        recipient = "",
        direction = "",
        directionDetail = null,
        directionPlaceId = null,
        latitude = lat,
        longitude = lng,
        order = 0,
        note = null,
    )

    @Test
    fun walk_ends_at_farthest_stop() {
        // Origen en (-34, -58); tres paradas sobre la misma calle, lng creciente.
        val stops = listOf(
            stop("A", -34.0, -58.001), // más cercana
            stop("B", -34.0, -58.002),
            stop("C", -34.0, -58.003), // más lejana
        )
        // Para WALK el destino es C; intermedios [A, B]. Google optimiza A → B.
        val result = resolveVisitOrder(
            travelMode = TravelMode.WALK,
            stops = stops,
            originLatitude = -34.0,
            originLongitude = -58.0,
            optimizedIntermediateWaypointIndex = listOf(0, 1),
        )
        // A, B, C → la más lejana queda última.
        assertEquals(listOf(0, 1, 2), result)
    }

    @Test
    fun walk_keeps_farthest_last_even_if_intermediates_reversed() {
        val stops = listOf(
            stop("A", -34.0, -58.001),
            stop("B", -34.0, -58.002),
            stop("C", -34.0, -58.003),
        )
        // Google devuelve B → A para los intermedios; C debe quedar al final igual.
        val result = resolveVisitOrder(
            travelMode = TravelMode.WALK,
            stops = stops,
            originLatitude = -34.0,
            originLongitude = -58.0,
            optimizedIntermediateWaypointIndex = listOf(1, 0),
        )
        assertEquals(listOf(1, 0, 2), result)
    }

    @Test
    fun walk_falls_back_when_google_indices_null() {
        val stops = listOf(
            stop("A", -34.0, -58.001),
            stop("B", -34.0, -58.002),
            stop("C", -34.0, -58.003),
        )
        val result = resolveVisitOrder(
            travelMode = TravelMode.WALK,
            stops = stops,
            originLatitude = -34.0,
            originLongitude = -58.0,
            optimizedIntermediateWaypointIndex = null,
        )
        assertEquals(listOf(0, 1, 2), result)
    }

    @Test
    fun drive_keeps_google_order_as_is() {
        val stops = listOf(
            stop("A", -34.0, -58.001),
            stop("B", -34.0, -58.002),
            stop("C", -34.0, -58.003),
        )
        val result = resolveVisitOrder(
            travelMode = TravelMode.DRIVE,
            stops = stops,
            originLatitude = -34.0,
            originLongitude = -58.0,
            optimizedIntermediateWaypointIndex = listOf(2, 1, 0),
        )
        assertEquals(listOf(2, 1, 0), result)
    }
}

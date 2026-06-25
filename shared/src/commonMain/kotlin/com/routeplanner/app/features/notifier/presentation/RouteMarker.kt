package com.routeplanner.app.features.notifier.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.routeplanner.app.ui.RoutePlannerTheme

// ---------------------------------------------------------------------------
// Tipos de marker para la ruta
// ---------------------------------------------------------------------------

enum class MarkerType {
    ORIGIN,       // verde — punto de partida
    DESTINATION,  // rojo  — punto de llegada
    WAYPOINT,     // azul  — parada intermedia numerada
}

// Paleta de colores centralizada
object MarkerColors {
    val origin      = Color(0xFF34A853)
    val destination = Color(0xFFEA4335)
    val waypoint    = Color(0xFF1A73E8)
    val white       = Color.White
    val shadow      = Color(0x33000000)
}

// ---------------------------------------------------------------------------
// Marker de origen — círculo verde con ícono de punto
// ---------------------------------------------------------------------------

@Composable
fun OriginMarker() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(RoutePlannerTheme.colors.secondary)
                .border(2.dp, MarkerColors.white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            /*Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MarkerColors.white),
            )*/
        }
        // Punta del pin
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(MarkerColors.origin),
        )
    }
}

// ---------------------------------------------------------------------------
// Marker de destino — círculo rojo con check
// ---------------------------------------------------------------------------

@Composable
fun DestinationMarker() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.Green)
                .border(2.dp, MarkerColors.white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            /*Text(
                text  = "✓",
                color = MarkerColors.white,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
            )*/
        }
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(MarkerColors.destination),
        )
    }
}

// ---------------------------------------------------------------------------
// Marker de parada numerada — círculo azul con número de orden
// ---------------------------------------------------------------------------

@Composable
fun WaypointMarker(order: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(MarkerColors.waypoint)
                .border(2.dp, MarkerColors.white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text       = order.toString(),
                color      = MarkerColors.white,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(6.dp)
                .background(MarkerColors.waypoint),
        )
    }
}

// ---------------------------------------------------------------------------
// ContentId constants — claves del mapa customMarkerContent
// ---------------------------------------------------------------------------

object MarkerContentId {
    const val ORIGIN      = "origin"
    const val DESTINATION = "destination"
    // Para waypoints: "waypoint_1", "waypoint_2", etc.
    fun waypoint(order: Int) = "waypoint_$order"
}
package com.routeplanner.app.features.home.presentation

import com.routeplanner.app.features.home.location.LocationCoordinates

// ---------------------------------------------------------------------------
// Estado de cada selector (Origen / Destino)
//
// Representa las 3 situaciones posibles que pide el mockup:
//   1. UsingCurrentLocation → "Usando ubicación actual" (con sus coords)
//   2. ManualAddress → el usuario buscó/eligió una dirección a mano
//   3. PermissionRequired → no hay permiso de ubicación; hay que pedirlo
//      o elegir una dirección manualmente como alternativa
// ---------------------------------------------------------------------------

sealed class LocationSelection {

    data object Empty : LocationSelection()

    data class UsingCurrentLocation(
        val coordinates: LocationCoordinates,
    ) : LocationSelection()

    data class ManualAddress(
        val address: String,
        val coordinates: LocationCoordinates,
    ) : LocationSelection()

    data object PermissionRequired : LocationSelection()
}

// ---------------------------------------------------------------------------
// Estado completo del diálogo "Nueva ruta"
// ---------------------------------------------------------------------------

data class CreateRouteState(
    val name: String = "",
    val origin: ManualAddress,
    val destination: ManualAddress,
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                origin.address != "" &&
                destination.address != ""
}

private fun LocationSelection.hasCoordinates(): Boolean = when (this) {
    is LocationSelection.UsingCurrentLocation -> true
    is LocationSelection.ManualAddress -> true
    LocationSelection.Empty,
    LocationSelection.PermissionRequired -> false
}

// Extrae las coordenadas finales, sea cual sea el origen de la selección.
// Devuelve null si todavía no hay una selección válida.
fun LocationSelection.coordinatesOrNull(): LocationCoordinates? = when (this) {
    is LocationSelection.UsingCurrentLocation -> coordinates
    is LocationSelection.ManualAddress -> coordinates
    LocationSelection.Empty,
    LocationSelection.PermissionRequired -> null
}

data class ManualAddress(
    val address: String,
    val coordinates: LocationCoordinates,
)
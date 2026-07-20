package com.routeplanner.app.features.home.domain.model

import kotlin.time.Instant

enum class RouteStateEnum(val id: Long, val description: String) {
    ACTIVE(1, "EN CURSO"),
    FINISHED(2, "FINALIZADA"),
    CANCELED(3, "CANCELADA");

    companion object {
        fun fromName(name: String): RouteStateEnum =
            entries.find { it.name == name }
                ?: throw IllegalArgumentException("Unknown route state: $name")

        fun fromId(id: Long): RouteStateEnum =
            entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown route state id: $id")
    }
}

data class NotifierRoute(
    val id: Long,
    //val userId: Long,
    val state: String,
    val name: String,
    val createdAt: Instant,
    val originDir: String,
    val originPlaceId: String?,
    val originLatitude: Double,
    val originLongitude: Double,
    val destinationDir: String,
    val destinationPlaceId: String?,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val notifierStops: List<NotifierStop>,
)
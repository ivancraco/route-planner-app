package com.routeplanner.app.core.utils

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun formatearFecha(instant: Instant): String {
    val fecha = instant.toLocalDateTime(TimeZone.UTC)

    val meses = listOf(
        "enero", "febrero", "marzo", "abril",
        "mayo", "junio", "julio", "agosto",
        "septiembre", "octubre", "noviembre", "diciembre"
    )

    return "${fecha.day} de ${meses[fecha.month.ordinal]} de ${fecha.year}"
}
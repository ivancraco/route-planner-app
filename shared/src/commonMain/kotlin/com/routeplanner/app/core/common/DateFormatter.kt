package com.routeplanner.app.core.common

// commonMain — DateFormatter.kt
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Instant.toArgentinaDateString(): String {
    val tz = TimeZone.of("America/Argentina/Buenos_Aires")
    val dt = this.toLocalDateTime(tz)
    return "${dt.dayOfMonth.toString().padStart(2, '0')}/" +
            "${dt.monthNumber.toString().padStart(2, '0')}/" +
            "${dt.year}"
}
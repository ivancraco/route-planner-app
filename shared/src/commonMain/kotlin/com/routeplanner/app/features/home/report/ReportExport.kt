package com.routeplanner.app.features.home.report

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import com.routeplanner.app.core.ui.UiPalette
import com.routeplanner.app.core.utils.formatearFecha
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary

data class ReportPalette(
    val background: Int,
    val surface: Int,
    val border: Int,
    val textPrimary: Int,
    val textSecondary: Int,
    val accent: Int,
    val onAccent: Int,
    val accentSoft: Int,
    val success: Int,
    val warning: Int,
    val danger: Int,
)

internal fun UiPalette.toReportPalette(): ReportPalette = ReportPalette(
    background = background.toArgb(),
    surface = surface.toArgb(),
    border = border.toArgb(),
    textPrimary = textPrimary.toArgb(),
    textSecondary = textSecondary.toArgb(),
    accent = accent.toArgb(),
    onAccent = onAccent.toArgb(),
    accentSoft = accentSoft.toArgb(),
    success = success.toArgb(),
    warning = warning.toArgb(),
    danger = danger.toArgb(),
)

// ─── Exportador multiplataforma ───────────────────────────────────────────

interface ReportExporter {
    /** Genera y guarda el PDF. Devuelve la ruta/URI del archivo, o null si falla. */
    suspend fun export(route: SupervisorRouteSummary, palette: ReportPalette): String?
}

@Composable
expect fun rememberReportExporter(): ReportExporter

// ─── Contenido del reporte (compartido por ambas plataformas) ─────────────

sealed interface ReportElement {
    data class Text(
        val value: String,
        val color: Int,
        val sizeSp: Float,
        val bold: Boolean = false,
        val topPaddingSp: Float = 0f,
    ) : ReportElement

    data class Divider(val color: Int) : ReportElement
}

fun buildReportElements(
    route: SupervisorRouteSummary,
    palette: ReportPalette,
): List<ReportElement> {
    val elements = mutableListOf<ReportElement>()

    elements += ReportElement.Text(route.name, palette.textPrimary, 20f, bold = true)
    elements += ReportElement.Text(
        value = "${route.user} · ${route.state}",
        color = palette.textSecondary,
        sizeSp = 13f,
        topPaddingSp = 4f,
    )
    elements += ReportElement.Text(
        value = formatearFecha(route.date),
        color = palette.textSecondary,
        sizeSp = 12f,
        topPaddingSp = 2f,
    )

    elements += ReportElement.Divider(palette.border)

    elements += ReportElement.Text("Recorrido", palette.textPrimary, 15f, bold = true, topPaddingSp = 6f)

    elements += ReportElement.Text("Origen", palette.textSecondary, 11f, topPaddingSp = 8f)
    elements += ReportElement.Text(shortAddress(route.origin), palette.textPrimary, 13f, bold = true, topPaddingSp = 2f)

    route.stops.sortedBy { it.order ?: Int.MAX_VALUE }.forEachIndexed { index, stop ->
        val number = stop.order ?: (index + 1)
        elements += ReportElement.Text("Parada $number", palette.textSecondary, 11f, topPaddingSp = 8f)
        elements += ReportElement.Text(shortAddress(stop.direction), palette.textPrimary, 13f, bold = true, topPaddingSp = 2f)
        elements += ReportElement.Text(stop.state, reportStatusColor(palette, stop.state), 11f, topPaddingSp = 2f)
    }

    //elements += ReportElement.Text("Destino", palette.textSecondary, 11f, topPaddingSp = 8f)
    //elements += ReportElement.Text(shortAddress(route.destination), palette.textPrimary, 13f, bold = true, topPaddingSp = 2f)

    return elements
}

fun reportStatusColor(palette: ReportPalette, state: String): Int {
    val s = state.lowercase()
    return when {
        s.isBlank() -> palette.textSecondary
        s.contains("no notific") || s.contains("pendiente") -> palette.warning
        s.contains("fall") || s.contains("no recib") -> palette.danger
        s.contains("notific") || s.contains("exit") || s.contains("entreg") -> palette.success
        else -> palette.textSecondary
    }
}

fun reportFileName(route: SupervisorRouteSummary): String {
    val safe = route.name
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
        .trim('_')
        .ifBlank { "ruta" }
    return "reporte_$safe.pdf"
}

private fun shortAddress(raw: String): String =
    raw.split(",").firstOrNull()?.trim()?.takeIf { it.isNotEmpty() } ?: raw

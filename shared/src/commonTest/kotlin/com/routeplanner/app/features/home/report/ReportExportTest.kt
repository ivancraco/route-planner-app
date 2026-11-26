package com.routeplanner.app.features.home.report

import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import com.routeplanner.app.features.home.domain.model.SupervisorStopSummary
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant

class ReportExportTest {

    private val palette = ReportPalette(
        background = 0xFFFFFFFF.toInt(),
        surface = 0xFFFFFFFF.toInt(),
        border = 0xFFE8EAED.toInt(),
        textPrimary = 0xFF14161A.toInt(),
        textSecondary = 0xFF6B7280.toInt(),
        accent = 0xFF2F5BEA.toInt(),
        onAccent = 0xFFFFFFFF.toInt(),
        accentSoft = 0xFFEAF0FF.toInt(),
        success = 0xFF1E9E6A.toInt(),
        warning = 0xFFC27C0E.toInt(),
        danger = 0xFFD64545.toInt(),
    )

    private val route = SupervisorRouteSummary(
        name = "Ruta Norte",
        user = "Carlos",
        date = Instant.fromEpochSeconds(0),
        origin = "Av. Siempreviva 742, Springfield",
        destination = "Calle Falsa 123, Springfield",
        state = "En curso",
        stops = listOf(
            SupervisorStopSummary(state = "Entregado", direction = "Parque Central, Centro", order = 1),
            SupervisorStopSummary(state = "Pendiente", direction = "Plaza Mayor, Centro", order = 2),
            SupervisorStopSummary(state = "Fallido", direction = "Mercado, Norte", order = 3),
        ),
    )

    @Test
    fun statusColor_maps_to_palette() {
        assertEquals(palette.success, reportStatusColor(palette, "Entregado"))
        assertEquals(palette.warning, reportStatusColor(palette, "Pendiente"))
        assertEquals(palette.danger, reportStatusColor(palette, "Fallido"))
        assertEquals(palette.textSecondary, reportStatusColor(palette, ""))
    }

    @Test
    fun file_name_is_sanitized() {
        assertEquals("reporte_Ruta_Norte.pdf", reportFileName(route))
        assertEquals("reporte_ruta.pdf", reportFileName(route.copy(name = "???")))
    }

    @Test
    fun report_contains_ordered_stops() {
        val elements = buildReportElements(route, palette)
        val texts = elements.filterIsInstance<ReportElement.Text>().map { it.value }
        assertTrue(texts.contains("Parada 1"))
        assertTrue(texts.contains("Parada 2"))
        assertTrue(texts.contains("Parada 3"))
        assertTrue(texts.contains("Destino"))
    }
}

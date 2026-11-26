package com.routeplanner.app.features.home.report

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.StaticLayout
import android.text.TextPaint
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import androidx.core.graphics.withSave

@Composable
actual fun rememberReportExporter(): ReportExporter {
    val context = LocalContext.current
    return remember(context) { AndroidReportExporter(context.applicationContext) }
}

class AndroidReportExporter(private val context: Context) : ReportExporter {

    override suspend fun export(route: SupervisorRouteSummary, palette: ReportPalette): String? =
        withContext(Dispatchers.IO) {
            val elements = buildReportElements(route, palette)
            val fileName = reportFileName(route)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveViaMediaStore(elements, fileName)
            } else {
                saveLegacy(elements, fileName)
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveViaMediaStore(elements: List<ReportElement>, fileName: String): String? {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: return null
        return try {
            resolver.openOutputStream(uri)?.use { output -> render(elements, output) }
            uri.toString()
        } catch (_: Exception) {
            null
        }
    }

    private fun saveLegacy(elements: List<ReportElement>, fileName: String): String? {
        return try {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!dir.exists()) dir.mkdirs()
            val file = java.io.File(dir, fileName)
            file.outputStream().use { output -> render(elements, output) }
            file.absolutePath
        } catch (_: Exception) {
            null
        }
    }

    private fun render(elements: List<ReportElement>, output: OutputStream) {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 48f
        val contentWidth = pageWidth - margin * 2
        val bottom = pageHeight - margin

        val pdf = PdfDocument()
        try {
            var page = pdf.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdf.pages.size + 1).create()
            )
            var canvas = page.canvas
            var y = margin

            fun newPageIfNeeded(extra: Float) {
                if (y + extra > bottom) {
                    pdf.finishPage(page)
                    page = pdf.startPage(
                        PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pdf.pages.size + 1).create()
                    )
                    canvas = page.canvas
                    y = margin
                }
            }

            for (element in elements) {
                when (element) {
                    is ReportElement.Text -> {
                        val paint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
                            color = element.color
                            textSize = element.sizeSp
                            typeface = if (element.bold) {
                                Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            } else {
                                Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                            }
                        }
                        val layout = StaticLayout.Builder
                            .obtain(element.value, 0, element.value.length, paint, contentWidth.toInt())
                            .build()

                        newPageIfNeeded(layout.height + element.topPaddingSp)
                        y += element.topPaddingSp
                        canvas.withSave {
                            translate(margin, y)
                            layout.draw(this)
                        }
                        y += layout.height
                    }

                    is ReportElement.Divider -> {
                        val paint = Paint().apply { color = element.color }
                        newPageIfNeeded(20f)
                        y += 10f
                        canvas.drawRect(margin, y, pageWidth - margin, y + 1f, paint)
                        y += 11f
                    }
                }
            }

            pdf.finishPage(page)
            pdf.writeTo(output)
        } finally {
            pdf.close()
        }
    }
}

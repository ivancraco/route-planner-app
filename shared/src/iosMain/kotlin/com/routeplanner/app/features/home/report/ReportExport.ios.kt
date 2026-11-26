@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.routeplanner.app.features.home.report

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.routeplanner.app.features.home.domain.model.SupervisorRouteSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSMutableAttributedString
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSMakeRange
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.writeToFile
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIGraphicsPDFRenderer
import platform.UIKit.UIGraphicsPDFRendererFormat
import platform.UIKit.UIRectFill
import platform.UIKit.boundingRectWithSize
import platform.UIKit.drawInRect
import platform.UIKit.popoverPresentationController
import platform.UIKit.NSStringDrawingUsesLineFragmentOrigin

@Composable
actual fun rememberReportExporter(): ReportExporter = remember { IosReportExporter() }

class IosReportExporter : ReportExporter {

    override suspend fun export(route: SupervisorRouteSummary, palette: ReportPalette): String? {
        val elements = buildReportElements(route, palette)
        val fileName = reportFileName(route)
        val path = withContext(Dispatchers.Default) { renderAndSave(elements, fileName) }
        if (path != null) {
            withContext(Dispatchers.Main) { presentShareSheet(path) }
        }
        return path
    }

    private fun renderAndSave(elements: List<ReportElement>, fileName: String): String? {
        val pageWidth = 595.0
        val pageHeight = 842.0
        val margin = 48.0
        val contentWidth = pageWidth - margin * 2
        val bottom = pageHeight - margin

        val format = UIGraphicsPDFRendererFormat()
        val renderer = UIGraphicsPDFRenderer(
            bounds = CGRectMake(0.0, 0.0, pageWidth, pageHeight),
            format = format,
        )
        val data = renderer.PDFDataWithActions { context ->
            context?.beginPage()
            var y = margin

            fun newPageIfNeeded(extra: Double) {
                if (y + extra > bottom) {
                    context?.beginPage()
                    y = margin
                }
            }

            for (element in elements) {
                when (element) {
                    is ReportElement.Text -> {
                        val font = if (element.bold) {
                            UIFont.boldSystemFontOfSize(element.sizeSp.toDouble())
                        } else {
                            UIFont.systemFontOfSize(element.sizeSp.toDouble())
                        }
                        val attributes: Map<Any?, Any> = mapOf<Any?, Any>(
                            NSFontAttributeName to font,
                            NSForegroundColorAttributeName to element.color.toUIColor(),
                        )
                        val attributed = NSMutableAttributedString().apply {
                            replaceCharactersInRange(NSMakeRange(0uL, 0uL), withString = element.value)
                            setAttributes(attributes, range = NSMakeRange(0uL, element.value.length.toULong()))
                        }
                        val bounds = attributed.boundingRectWithSize(
                            size = CGSizeMake(contentWidth, 10000.0),
                            options = NSStringDrawingUsesLineFragmentOrigin,
                            context = null,
                        )
                        val height = CGRectGetHeight(bounds)

                        newPageIfNeeded(height + element.topPaddingSp.toDouble())
                        y += element.topPaddingSp.toDouble()
                        attributed.drawInRect(CGRectMake(margin, y, contentWidth, height))
                        y += height
                    }

                    is ReportElement.Divider -> {
                        newPageIfNeeded(20.0)
                        y += 10.0
                        element.color.toUIColor().setFill()
                        UIRectFill(CGRectMake(margin, y, contentWidth, 1.0))
                        y += 11.0
                    }
                }
            }
        }

        val docsDir = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true,
        ).firstOrNull() as? String ?: return null
        val path = "$docsDir/$fileName"
        return if (data.writeToFile(path, atomically = true)) path else null
    }

    private fun presentShareSheet(path: String) {
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController ?: return
        val fileUrl = NSURL.fileURLWithPath(path)
        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )
        activityViewController.popoverPresentationController?.sourceView = rootViewController.view
        rootViewController.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null,
        )
    }
}

private fun Int.toUIColor(): UIColor = UIColor(
    red = ((this shr 16) and 0xFF) / 255.0,
    green = ((this shr 8) and 0xFF) / 255.0,
    blue = (this and 0xFF) / 255.0,
    alpha = ((this shr 24) and 0xFF) / 255.0,
)

package edu.cit.velasco.paystream.features.payroll

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.text.DecimalFormat

object PdfGeneratorTool {

    fun generateAndSavePayslip(context: Context, payslip: PayrollTransactionResponse) {
        val df = DecimalFormat("#,##0.00")
        val firstName = payslip.employee.user.firstname ?: "Unknown"
        val lastName = payslip.employee.user.lastname ?: ""
        val fileName = "Payslip_${firstName}_${lastName}_${payslip.monthYear}.pdf"

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(600, 850, 1).create()
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply { textSize = 26f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = Color.BLACK }
        val subTitlePaint = Paint().apply { textSize = 14f; color = Color.parseColor("#6B7280") }
        val headerPaint = Paint().apply { textSize = 16f; typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); color = Color.BLACK }
        val normalText = Paint().apply { textSize = 14f; color = Color.BLACK }

        var currentY = 60f
        canvas.drawText("PayStream", 40f, currentY, titlePaint)
        currentY += 20f
        canvas.drawText("Official Employee Payslip", 40f, currentY, subTitlePaint)

        currentY += 60f

        // 🟢 THE FIX: Read the snapshotted position from the transaction, fallback if old legacy record
        val jobTitle = (payslip.positionAtTime ?: payslip.employee.position).uppercase()
        canvas.drawText("$firstName $lastName - $jobTitle", 40f, currentY, headerPaint)

        currentY += 40f
        val rightMargin = 560f

        // 1. EARNINGS SECTION (Using locked historical data)
        val rBase = payslip.rateBase ?: BigDecimal.ZERO
        val r40 = payslip.rate40ft ?: BigDecimal.ZERO
        val r20 = payslip.rate20ft ?: BigDecimal.ZERO
        val rOtH = payslip.rateOtHour ?: BigDecimal.ZERO
        val rOtC = payslip.rateOtContainer ?: BigDecimal.ZERO

        val pBase = payslip.payBase ?: BigDecimal.ZERO
        val p40 = payslip.pay40ft ?: BigDecimal.ZERO
        val p20 = payslip.pay20ft ?: BigDecimal.ZERO
        val pOtH = payslip.payOtHour ?: BigDecimal.ZERO
        val pOtC = payslip.payOtContainer ?: BigDecimal.ZERO

        // Draw Base
        if (payslip.workingDays > BigDecimal.ZERO) {
            canvas.drawText("Base (${payslip.workingDays} x Php ${df.format(rBase)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(pBase)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        // Draw 40ft
        if (payslip.count40ft > BigDecimal.ZERO) {
            canvas.drawText("40ft Container (${payslip.count40ft} x Php ${df.format(r40)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(p40)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        // Draw 20ft
        if (payslip.count20ft > BigDecimal.ZERO) {
            canvas.drawText("20ft Container (${payslip.count20ft} x Php ${df.format(r20)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(p20)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        // Draw OT Hours
        if (payslip.overtimeHours > BigDecimal.ZERO) {
            canvas.drawText("Overtime Hours (${payslip.overtimeHours} x Php ${df.format(rOtH)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(pOtH)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        // Draw OT Containers
        if (payslip.otContainerCount > BigDecimal.ZERO) {
            canvas.drawText("Overtime Container (${payslip.otContainerCount} x Php ${df.format(rOtC)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(pOtC)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        // Draw Out of Town
        if (payslip.outOfTownTrips > BigDecimal.ZERO) {
            canvas.drawText("Out of Town", 40f, currentY, normalText)
            drawRightAligned(canvas, "Php ${df.format(payslip.outOfTownTrips)}", rightMargin, currentY, normalText)
            currentY += 25f
        }

        currentY += 20f

        // 2. DEDUCTIONS SECTION
        if (payslip.absences > BigDecimal.ZERO) {
            val absAmount = payslip.absenceDeductionAmount ?: BigDecimal.ZERO
            canvas.drawText("Absences (${payslip.absences} x -Php ${df.format(rBase)})", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(absAmount)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        if (payslip.cashAdvance > BigDecimal.ZERO) {
            canvas.drawText("Cash Advance", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(payslip.cashAdvance)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        if (payslip.otherDebts > BigDecimal.ZERO) {
            canvas.drawText("Other Debts", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(payslip.otherDebts)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        if (payslip.sssDeduction > BigDecimal.ZERO) {
            canvas.drawText("SSS", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(payslip.sssDeduction)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        if (payslip.philhealthDeduction > BigDecimal.ZERO) {
            canvas.drawText("PhilHealth", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(payslip.philhealthDeduction)}", rightMargin, currentY, normalText)
            currentY += 25f
        }
        if (payslip.pagibigDeduction > BigDecimal.ZERO) {
            canvas.drawText("PagIBIG", 40f, currentY, normalText)
            drawRightAligned(canvas, "- Php ${df.format(payslip.pagibigDeduction)}", rightMargin, currentY, normalText)
            currentY += 25f
        }

        currentY += 30f

        // 3. Total Amount
        canvas.drawText("TOTAL AMOUNT", 40f, currentY, headerPaint)
        drawRightAligned(canvas, "Php ${df.format(payslip.netPay)}", rightMargin, currentY, headerPaint)

        // 4. Signatures
        currentY = 740f
        canvas.drawText("Prepared by:", 40f, currentY, subTitlePaint)
        canvas.drawText("Received by:", 350f, currentY, subTitlePaint)

        currentY += 40f
        canvas.drawText("Admin", 40f, currentY, headerPaint)
        canvas.drawText("$firstName $lastName", 350f, currentY, headerPaint)

        document.finishPage(page)
        savePdfToDownloads(context, document, fileName)
    }

    private fun drawRightAligned(canvas: Canvas, text: String, rightX: Float, y: Float, paint: Paint) {
        val width = paint.measureText(text)
        canvas.drawText(text, rightX - width, y, paint)
    }

    private fun savePdfToDownloads(context: Context, document: PdfDocument, fileName: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
                    Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadsDir, fileName)
                FileOutputStream(file).use { document.writeTo(it) }
                Toast.makeText(context, "Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving PDF: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            document.close()
        }
    }
}
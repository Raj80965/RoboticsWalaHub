package com.roboticswala.hub.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.roboticswala.hub.data.models.SessionQRData
import java.util.EnumMap

object QRCodeGenerator {

    private const val QR_PREFIX = "ROBOTICS_WALA_ATTENDANCE"

    /**
     * Encodes SessionQRData into a standardized payload string.
     */
    fun encodeSessionPayload(session: SessionQRData): String {
        return "$QR_PREFIX|${session.sessionId}|${session.sessionToken}|${session.labName}|${session.expiresAt}"
    }

    /**
     * Decodes QR payload back into SessionQRData, or null if invalid format.
     */
    fun decodeSessionPayload(payload: String): SessionQRData? {
        val trimmed = payload.trim()
        if (!trimmed.startsWith(QR_PREFIX)) return null
        val parts = trimmed.split("|")
        if (parts.size < 5) return null
        return try {
            SessionQRData(
                sessionId = parts[1],
                sessionToken = parts[2],
                labName = parts[3],
                expiresAt = parts[4].toLongOrNull() ?: 0L
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Generates a square QR Code Bitmap from raw text.
     */
    fun generateQRCodeBitmap(
        content: String,
        size: Int = 512,
        primaryColor: Int = Color.BLACK,
        backgroundColor: Int = Color.WHITE
    ): Bitmap? {
        if (content.isBlank()) return null
        return try {
            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
                put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
                put(EncodeHintType.MARGIN, 2)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val pixels = IntArray(width * height)

            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) primaryColor else backgroundColor
                }
            }

            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

package com.example.attendanceapp.ui.selfie

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class OverlayView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // Paint untuk outline/border persegi rounded
    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    // Paint untuk area semi-transparan diluar fokus
    private val transparentPaint = Paint().apply {
        color = Color.BLACK
        alpha = 150 // Semi-transparent black for outside area
    }

    // Paint untuk membuat efek "lubang" transparan
    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    // RectF untuk menyimpan dimensi persegi rounded
    val rectF = RectF()

    // Radius sudut persegi rounded
    private val cornerRadius = 40f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Hitung dimensi persegi panjang
        val rectWidth = width * 0.85f
        val rectHeight = height * 0.55f

        // Hitung posisi persegi (tengah layar)
        val left = (width - rectWidth) / 2
        val top = (height - rectHeight) / 2
        val right = left + rectWidth
        val bottom = top + rectHeight

        // Set dimensi ke rectF
        rectF.set(left, top, right, bottom)

        // Simpan state canvas
        val saveCount = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        // Gambar background semi-transparan menutupi seluruh view
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), transparentPaint)

        // Buat "lubang" berbentuk persegi rounded menggunakan PorterDuff.Mode.CLEAR
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, clearPaint)

        // Gambar border persegi rounded
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, borderPaint)

        // Kembalikan state canvas
        canvas.restoreToCount(saveCount)
    }
}
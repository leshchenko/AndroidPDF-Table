package com.leshchenko.pdftable

import android.graphics.Canvas
import android.graphics.PointF

class Row(
    var preferences: Preferences? = null,
    private val columns: List<Column>
) {
    var width = 0f
    var height = 0f

    lateinit var startPoint: PointF

    fun drawRow(canvas: Canvas) {
        columns.forEach { column ->
            if (startPoint.y + height >= canvas.height) {
                return
            }
            column.startPoint = startPoint
            if (column.preferences == null) {
                column.preferences = preferences
            }
            column.drawColumn(canvas)
            startPoint = PointF(startPoint.x + column.width, startPoint.y)
        }
    }

    fun setRowPreferences(preferences: Preferences) {
        if (this.preferences == null) this.preferences = preferences
        columns.forEach { it.setColumnPreferences(this.preferences ?: preferences) }
    }

    fun setRowWidth(width: Float) {
        this.width = width
        columns.forEach { it.setColumnWidth(width / columns.size) }
    }

    fun calculateHeight() {
        height = columns.maxBy { it.getEstimatedHeight() }?.getEstimatedHeight() ?: 0f
        columns.forEach { it.setColumnHeight(height) }
    }

    fun isDrawn() = columns.all { it.isDrawn() }
}
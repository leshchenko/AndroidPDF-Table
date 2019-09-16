package com.leshchenko.pdftable

import android.graphics.Canvas
import android.graphics.PointF


class Column(
    var preferences: Preferences? = null,
    private val cells: List<Cell>
) {
    var width = 0f
    var height = 0f
    lateinit var startPoint: PointF

    fun drawColumn(canvas: Canvas) {
        cells.forEach { cell ->
            cell.startPoint = startPoint
            if (cell.preferences == null) {
                cell.preferences = preferences
            }
            cell.drawCell(canvas)
            startPoint = PointF(startPoint.x, startPoint.y + cell.cellHeight)
        }
    }

    fun setColumnWidth(width: Float) {
        this.width = width
        cells.forEach { it.width = width }
    }

    fun getEstimatedHeight() = cells.sumBy { it.getEstimatedHeight().toInt() }.toFloat()

    fun setColumnHeight(height: Float) {
        this.height = height
        cells.forEach { it.cellHeight = height / cells.size }
    }

    fun isDrawn() = cells.all { it.isDrawn }
    fun setColumnPreferences(preferences: Preferences) {
        if (this.preferences == null) this.preferences = preferences
        cells.forEach { it.setCellPreferences(this.preferences ?: preferences) }
    }
}
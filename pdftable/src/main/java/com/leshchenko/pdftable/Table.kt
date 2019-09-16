package com.leshchenko.pdftable

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.util.Log
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.math.ceil
import kotlin.math.roundToInt

class Table(
    private val pageSize: Size = Size.A4(), private val file: FileOutputStream,
    private val preferences: Preferences = Preferences(),
    private val rows: List<Row>
) {
    private var pageNumber = 1

    init {
        rows.forEach {
            it.setRowWidth(getTableWidth())
            it.setRowPreferences(preferences)
        }
        rows.forEach { it.calculateHeight() }
    }

    private var startPoint = PointF().apply {
        x = preferences.tableMargins.left
        y = preferences.tableMargins.top
    }

    fun drawTable() {
        with(PdfDocument()) {
            drawRows(this)
            writeTo(file)
            close()
        }
    }

    private fun drawRows(document: PdfDocument) {
        val page: PdfDocument.Page = document.startPage(pageInfo())
        rows.forEach { row ->
            if (row.isDrawn().not()) {

                row.startPoint = startPoint
                row.drawRow(page.canvas)
                startPoint = PointF(startPoint.x, startPoint.y + row.height)
            }
        }
        document.finishPage(page)
        pageNumber++
        startPoint = PointF(preferences.tableMargins.left, preferences.tableMargins.top)

        if (isDrawn().not()) {
            drawRows(document)
        }
    }

    private fun isDrawn() = rows.all { it.isDrawn() }

    private fun getTableWidth() =
        pageInfo().pageWidth - preferences.tableMargins.top - preferences.tableMargins.bottom

    private fun pageInfo() =
        PdfDocument.PageInfo.Builder(pageSize.width, pageSize.height, pageNumber).create()
}
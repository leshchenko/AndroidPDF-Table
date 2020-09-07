package com.leshchenko.pdftable

import android.graphics.*
import android.util.Log
import java.io.FileInputStream
import java.io.FileNotFoundException
import kotlin.math.ceil
import kotlin.math.roundToInt

class Cell(
        var preferences: Preferences? = null,
        private val data: String,
        private val dataType: DataTypes = DataTypes.TEXT
) {
    var width = 0f
    var cellHeight = 0f
    var isDrawn = false
    lateinit var startPoint: PointF

    private val _preferences: Preferences
        get() = preferences ?: Preferences()

    fun drawCell(canvas: Canvas) {
        when (dataType) {
            DataTypes.IMAGE -> drawImage(canvas)
            DataTypes.TEXT -> drawText(canvas)
        }
    }

    private fun drawImage(canvas: Canvas) {
        val scale = getImageScale()
        val bottom = startPoint.y + cellHeight
        if (bottom >= canvas.height) {
            isDrawn = false
            return
        }
        val right = startPoint.x + width
        val top = startPoint.y + _preferences.textMargin.top
        val left = when (_preferences.alignType) {
            AlignTypes.LEFT -> startPoint.x + _preferences.textMargin.left
            AlignTypes.CENTER -> startPoint.x + (width / 2) - (getImageSize(scale).width / 2)
            AlignTypes.RIGHT -> startPoint.x + width - getImageSize(scale).width - _preferences.textMargin.right
        }

        val rect = RectF(startPoint.x, startPoint.y, right, bottom)
        if (_preferences.drawBorders) canvas.drawRect(rect, bordersPaint)
        canvas.drawRect(
                rect.left + _preferences.lineWidth / 2,
                rect.top + _preferences.lineWidth / 2,
                rect.right - _preferences.lineWidth / 2,
                rect.bottom - _preferences.lineWidth / 2,
                backgroundPaint
        )

        val outputOptions = BitmapFactory.Options()
        outputOptions.inSampleSize = scale
        BitmapFactory.decodeStream(FileInputStream(data), null, outputOptions)?.let {
            canvas.drawBitmap(it, left, top, paint)
        }
        isDrawn = true
    }

    private fun drawText(canvas: Canvas) {
        val bottom =
                startPoint.y + cellHeight
        if (bottom >= canvas.height) {
            isDrawn = false
            return
        }
        val right = startPoint.x + width

        val rect = RectF(startPoint.x, startPoint.y, right, bottom)
        var topY = rect.top + _preferences.textMargin.top
        if (_preferences.drawBorders) canvas.drawRect(rect, bordersPaint)
        canvas.drawRect(
                rect.left + _preferences.lineWidth / 2,
                rect.top + _preferences.lineWidth / 2,
                rect.right - _preferences.lineWidth / 2,
                rect.bottom - _preferences.lineWidth / 2,
                backgroundPaint
        )

        val stringLines = getStringLines()
        val textX = when (_preferences.alignType) {
            AlignTypes.LEFT -> rect.left + _preferences.textMargin.left
            AlignTypes.CENTER -> rect.left + (width / 2) - (paint.getTextSize(stringLines.first()).width() / 2)
            AlignTypes.RIGHT -> rect.left + width - paint.getTextSize(stringLines.first()).width() - _preferences.textMargin.right
        }

        stringLines.forEach { textLine ->
            canvas.drawText(
                    textLine,
                    textX,
                    topY + paint.getTextSize(textLine).height(),
                    paint
            )
            topY += paint.getTextSize(textLine).height() + _preferences.verticalTextSpacing
        }
        isDrawn = true
    }

    private fun getImageScale(): Int {
        var scale = 1
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(FileInputStream(data), null, options)
            if (options.outWidth >= getMaxContentWidth()) {
                scale = ceil(options.outWidth / getMaxContentWidth().toDouble()).toInt()
            }
            val imageHeight = options.outHeight / scale

            if (imageHeight >= maxContentHeight()) {
                scale = ceil(options.outHeight / maxContentHeight()).toInt()
            }
            if (imageHeight / scale > getMaxHeight()) {
                scale = ceil(options.outHeight / getMaxHeight()).toInt()
            }
        } catch (e: FileNotFoundException) {
            Log.e("Table", "decodeFile: $e")
        }
        return scale
    }

    private fun maxContentHeight() =
            cellHeight - _preferences.textMargin.top - _preferences.textMargin.bottom

    private fun getMaxContentWidth() =
            width - _preferences.textMargin.left - _preferences.textMargin.right

    private fun getTextHeight(): Float {
        val textSize = paint.getTextSize(data)
        if (textSize.width() > getMaxContentWidth()) {
            val stringLines = getStringLines()
            return ((textSize.height() + _preferences.verticalTextSpacing) * stringLines.size.toFloat())
        } else {
            return textSize.height().toFloat()
        }

    }

    private fun getStringLines(): MutableList<String> {
        val stringLines = mutableListOf<String>()

        var line = ""
        data.split(" ").forEach { word ->
            line += (if (line.isEmpty()) word else " $word")
                    .replace(System.lineSeparator(), "")
            if (paint.getTextSize(line + word).width() > getMaxContentWidth() ||
                    word.endsWith(System.lineSeparator())) {
                stringLines.add(line)
                line = ""
            }
        }
        if (line.isNotEmpty()) stringLines.add(line)
        return stringLines
    }

    fun getEstimatedHeight(): Float {
        return when (dataType) {
            DataTypes.TEXT -> {
                getTextHeight() + _preferences.textMargin.top + _preferences.textMargin.bottom
            }
            DataTypes.IMAGE -> {
                val imageSize = getImageSize()
                val scale = if (imageSize.width >= width) {
                    (imageSize.width / width).roundToInt()
                } else 1
                val imageHeight = imageSize.height / scale

                if (imageHeight > getMaxHeight()) {
                    getMaxHeight()
                } else {
                    imageHeight.toFloat() + _preferences.textMargin.bottom + _preferences.textMargin.top
                }
            }
        }
    }

    private fun getImageSize(scale: Int = 1): Size {
        val bitmapOptions = BitmapFactory.Options().also {
            it.inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(data, bitmapOptions)
        return Size(bitmapOptions.outWidth / scale, bitmapOptions.outHeight / scale)
    }

    private fun getMaxHeight() =
            A4_HEIGHT_IN_PIXELS - _preferences.tableMargins.top - _preferences.tableMargins.bottom

    fun setCellPreferences(preferences: Preferences) {
        if (this.preferences == null) this.preferences = preferences
    }

    private val backgroundPaint by lazy {
        Paint().also { paint ->
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.color = _preferences.backgroundColor
        }
    }

    private val bordersPaint by lazy {
        Paint().also { paint ->
            paint.isAntiAlias = true
            paint.strokeWidth = _preferences.lineWidth
            paint.style = Paint.Style.STROKE
            paint.color = Color.BLACK
        }
    }

    private val paint by lazy {
        Paint().also { paint ->
            paint.isAntiAlias = true
            with(_preferences) {
                paint.color = textColor
                paint.typeface = typeface
                paint.textSize = textSize
            }
        }
    }
}
package com.leshchenko.pdftable

import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt

class Size(val width: Int, val height: Int) {
    companion object {
        fun A4() = Size(A4_WIDTH_IN_PIXELS, A4_HEIGHT_IN_PIXELS)
    }
}

class Margins(
    val left: Float = 8f,
    val top: Float = 8f,
    val right: Float = 8f,
    val bottom: Float = 8f
)

class Preferences(
    @ColorInt val textColor: Int = Color.BLACK,
    val textSize: Float = 10f,
    val typeface: Typeface = Typeface.DEFAULT,
    val underLinedText: Boolean = false,
    val lineWidth: Float = 1f,
    @ColorInt val backgroundColor: Int = Color.WHITE,
    val textMargin: Margins = Margins(),
    val alignType: AlignTypes = AlignTypes.LEFT,
    val verticalTextSpacing: Float = 4f,
    val tableMargins: Margins = Margins(),
    val drawBorders: Boolean = true
)

enum class DataTypes { IMAGE, TEXT }
enum class AlignTypes { LEFT, CENTER, RIGHT }
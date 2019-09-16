package com.leshchenko.pdftable

import android.graphics.Paint
import android.graphics.Rect

const val A4_WIDTH_IN_PIXELS = 595
const val A4_HEIGHT_IN_PIXELS = 842

fun Paint.getTextSize(text: String) = Rect().also {
    getTextBounds(text, 0, text.length, it)
    return it
}

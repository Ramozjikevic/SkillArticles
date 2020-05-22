package ru.skillbranch.skillarticles.markdown.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting


class OrderedListSpan(
    @Px
    private val gapWidth: Float,
    private val order: String,
    @ColorInt
    private val orderColor: Int
) : LeadingMarginSpan {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)

    private var offset: Int =0

    override fun getLeadingMargin(first: Boolean): Int {
        return (gapWidth * order.length.inc() + offset).toInt()
    }

    override fun drawLeadingMargin(
        canvas: Canvas, paint: Paint, currentMarginLocation: Int, paragraphDirection: Int,
        lineTop: Int, lineBaseline: Int, lineBottom: Int, text: CharSequence?, lineStart: Int,
        lineEnd: Int, isFirstLine: Boolean, layout: Layout?
    ) {
        if (isFirstLine) {
            paint.withCustomColor {
                canvas.drawText(
                    "$order.",
                    gapWidth + currentMarginLocation.toFloat(),
                    lineBaseline.toFloat(),
                    paint
                )
            }
        }

        offset = paint.measureText("$order ").toInt()
    }

    private inline fun Paint.withCustomColor(block: () -> Unit) {
        val oldColor = color
        val oldStyle = style
        color = orderColor
        style = Paint.Style.FILL

        block()

        color = oldColor
        style = oldStyle
    }
}
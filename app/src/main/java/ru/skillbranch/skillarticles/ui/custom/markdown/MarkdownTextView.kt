package ru.skillbranch.skillarticles.ui.custom.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Spanned
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.graphics.withTranslation

@SuppressLint("AppCompatCustomView")
class MarkdownTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private val searchBgHelper = SearchBgHelper(context) {

    }

    override fun onDraw(canvas: Canvas) {
        if (text is Spanned && layout != null) {
            //Фунция по смещает канву отрисовывает и возвращает в исходную точку
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingRight.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }

}
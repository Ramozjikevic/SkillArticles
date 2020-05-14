package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(
    left:Int = marginLeft,
    top : Int = marginTop,
    right : Int = marginRight,
    bottom : Int = marginBottom
){
    (layoutParams as? ViewGroup.MarginLayoutParams)?.run{
        leftMargin = left
        rightMargin = right
        topMargin = top
        bottomMargin = bottom
    }
    //requestLayout()
}
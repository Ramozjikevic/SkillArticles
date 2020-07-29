package ru.skillbranch.skillarticles.ui.custom.markdown

import android.text.Spannable


interface IMarkdownView {
    var fontSize: Float
    val spannableContent: Spannable

    fun renderSearchResult(
        results: List<Pair<Int, Int>>,
        offset: Int
    ) {
        //TODO implement me
    }

    fun renderSearchPosition(
        searchPosition: Pair<Int, Int>,
        offset: Int
    ){
        //TODO implement me
    }

    fun clearSearchResult(){
        //TODO implement me
    }
}
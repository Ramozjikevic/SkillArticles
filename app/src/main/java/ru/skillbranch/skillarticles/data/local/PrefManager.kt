package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PrefManager(context: Context) {
    internal val preferences : SharedPreferences by lazy { PreferenceManager(context).sharedPreferences }
    var counter = 0

    fun clearAll(){
        preferences.edit().clear().apply()
    }
}
package ru.skillbranch.skillarticles.data.local

import android.content.Context
import androidx.preference.PreferenceManager

class PrefManager(context: Context) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var counter = 0

    fun clearAll(){
        preferences.edit().clear().apply()
    }

    fun <T> putValue(
        value: T,
        key: String = counter.inc().toString()
    ) = with(preferences.edit()) {
        when(value) {
            is String -> putString(key,value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitives types can be stored in Shared Preferences")
        }
        apply()
    }

}
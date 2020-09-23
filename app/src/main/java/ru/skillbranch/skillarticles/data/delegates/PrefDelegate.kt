package ru.skillbranch.skillarticles.data.delegates

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefDelegate<T>(private val defaultValue: T) {
    private var storedValue: T? = null

    operator fun provideDelegate(
        thisRef: PrefManager,
        prop: KProperty<*>
    ): ReadWriteProperty<PrefManager, T?> {
        val key = prop.name
        return object : ReadWriteProperty<PrefManager, T?> {
            override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
                if (storedValue == null) {
                    @Suppress("UNCHECKED_CAST")
                    storedValue = when(defaultValue) {
                        is Int -> thisRef.preferences.getInt(key,defaultValue as Int) as T
                        is Long -> thisRef.preferences.getLong(key,defaultValue as Long) as T
                        is Float -> thisRef.preferences.getFloat(key,defaultValue as Float) as T
                        is String -> thisRef.preferences.getString(key,defaultValue as String) as T
                        is Boolean -> thisRef.preferences.getBoolean(key,defaultValue as Boolean) as T
                        else -> error("This type can not be stored into Preferences")
                    }
                }
                return storedValue
            }

            override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
                with(thisRef.preferences.edit()) {
                    when (value) {
                        is String -> putString(key, value)
                        is Int -> putInt(key, value)
                        is Boolean -> putBoolean(key, value)
                        is Long -> putLong(key, value)
                        is Float -> putFloat(key, value)
                        else -> error("Only primitive types can be stored into Preferences")
                    }
                    apply()
                }
                storedValue = value
            }

        }
    }

}

class PrefLiveDelegate<T>(
    private val fieldKey: String,
    private val defaultValue: T,
    private val preferences: SharedPreferences
) : ReadOnlyProperty<Any?, LiveData<T>> {
    private var storedValue: LiveData<T>? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): LiveData<T> {
        if (storedValue == null) {
            storedValue = SharedPreferenceLiveData(preferences, fieldKey, defaultValue)
        }
        return storedValue!!
    }
}

internal class SharedPreferenceLiveData<T>(
    var sharedPrefs: SharedPreferences,
    var key: String,
    var defValue: T
) : LiveData<T>() {
    private val preferencesChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, shKey ->
            if(shKey == key) {
                value = readValue(defValue)
            }
        }

    override fun onActive() {
        super.onActive()
        value = readValue(defValue)
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferencesChangeListener)
    }

    override fun onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferencesChangeListener)
        super.onInactive()
    }

    @Suppress("UNCHECKED_CAST")
    private fun readValue(defaultValue: T): T {
        return when (defaultValue) {
            is Int -> sharedPrefs.getInt(key, defaultValue as Int) as T
            is Long -> sharedPrefs.getLong(key, defaultValue as Long) as T
            is Float -> sharedPrefs.getFloat(key, defaultValue as Float) as T
            is String -> sharedPrefs.getString(key, defaultValue as String) as T
            is Boolean -> sharedPrefs.getBoolean(key, defaultValue as Boolean) as T
            else -> error("This type $defaultValue can not be stored into Preferences")
        }
    }
}
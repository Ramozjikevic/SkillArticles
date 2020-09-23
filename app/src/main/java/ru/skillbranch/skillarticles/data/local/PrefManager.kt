package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.delegates.PrefDelegate
import ru.skillbranch.skillarticles.data.delegates.PrefLiveDelegate
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var isAuth by PrefDelegate(false)
    var isDarkMode by PrefDelegate(false)
    var isBigText by PrefDelegate(false)

    val isAuthLive: LiveData<Boolean> by PrefLiveDelegate("isAuth", false, preferences)

    val isDarkModeLive: LiveData<Boolean> by PrefLiveDelegate("isDarkMode", false, preferences)
    val isBigTextLive: LiveData<Boolean> by PrefLiveDelegate("isBigText", false, preferences)

    val appSettings = MediatorLiveData<AppSettings>().apply {
        value = AppSettings()

        addSource(isDarkModeLive) {
            value = value!!.copy(isDarkMode = it)
        }
        addSource(isBigTextLive) {
            value = value!!.copy(isBigText = it)
        }
    }.distinctUntilChanged()

    fun clearAll() {
        preferences.edit().clear().apply()
    }

    fun isAuth(): LiveData<Boolean> {
        return isAuthLive
    }

    fun setAuth(auth: Boolean): Unit {
        isAuth = auth
    }

    fun updateSettings(settings: AppSettings) {
        isDarkMode = settings.isDarkMode
        isBigText = settings.isBigText
    }
}
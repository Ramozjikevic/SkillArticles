package ru.skillbranch.skillarticles.data.local

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.models.AppSettings

object PrefManager {
    internal val preferences : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun clearAll(){
        preferences.edit().clear().apply()
    }

    fun getAppSettings() : LiveData<AppSettings> {
        //TODO implement
        return MutableLiveData(AppSettings())
    }

    fun isAuth() : MutableLiveData<Boolean> {
        //TODO implement
        return MutableLiveData(false)
    }

    fun setAuth(auth: Boolean): Unit {
        //TODO implement
    }
}
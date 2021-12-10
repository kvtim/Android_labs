package com.example.ppo1

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.preference.PreferenceManager
import java.util.*

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val language = sharedPreferences.getString("language", "en")
        val fontSize = sharedPreferences.getString("fontSize", "normal").toString()

        BaseActivity.dLocale = Locale(language)
        BaseActivity.dFontSize = fontSize
    }
}
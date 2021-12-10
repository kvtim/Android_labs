package com.example.ppo1

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.preference.PreferenceManager
import com.example.ppo1.util.LocaleUtil
import java.util.*
import android.util.DisplayMetrics
import android.view.WindowManager

open class BaseActivity : AppCompatActivity() {

    companion object {
        var dLocale: Locale? = null
        var dFontSize: String = "normal"
    }

    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: ContextThemeWrapper) {

        if(dLocale==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(dLocale)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        when (dFontSize) {
            "big" -> configuration.fontScale = 1.5F
            "normal" -> configuration.fontScale = 1.0F
            "small" -> configuration.fontScale = 0.5F
        }

        wrapper.applyOverrideConfiguration(configuration)
    }


/*    fun updateFontSize(wrapper: ContextThemeWrapper) {

         resources.configuration.fontScale = dFontSize  //0.85 small size, 1 normal size, 1,15 big etc

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = resources.configuration.fontScale * metrics.density
        baseContext.resources.updateConfiguration(resources.configuration, metrics)
    }*/

/*    //LocaleConfigurationUtil.class
    open fun adjustFontSize(context: Context): Context? {
        val configuration = context.resources.configuration
        // This will apply to all text like -> Your given text size * fontScale
        configuration.fontScale = dFontSize
        return context.createConfigurationContext(configuration)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(adjustFontSize(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontSize(this)
    }*/
}


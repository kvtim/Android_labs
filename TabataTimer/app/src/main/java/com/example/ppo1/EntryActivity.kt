package com.example.ppo1

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_timer.*

class EntryActivity : BaseActivity() {

    var isOpenApp = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val isDark = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("theme", false)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        setContentView(R.layout.activity_entry)
        hideSystemUI()

        val timer = object : CountDownTimer((3 * 1000).toLong(), 1000) {
            override fun onTick(p0: Long) {
                val seconds = p0 / 1000
            }

            override fun onFinish() {
                isOpenApp = false
                openApp()
            }
        }
        (timer as CountDownTimer).start()
    }

    override fun onResume() {
        super.onResume()
        if (!isOpenApp)
            finish()
    }

    private fun openApp() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
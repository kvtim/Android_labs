package com.example.ppo1

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Global.getInt
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.marginLeft
import com.example.ppo1.util.PrefUtil
import com.example.ppo1.util.convertMinutesToSeconds
import com.example.ppo1.util.convertSecondsToMinutes
import com.example.ppo1.util.getTimeFromStr
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timer.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        if (SettingsActivity.goBack) {
            SettingsActivity.goBack = false
            /*val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)*/
            this.finish()
        }

        setContentView(R.layout.activity_main)

        Log.d("Debug", "MainActivity onCreate")

        createNewB.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            intent.putExtra("FILE_NAME", "")
            this.startActivity(intent)
        }

        settingsB.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            this.startActivity(intent)
        }
    }

    override fun onResume() {
        if (SettingsActivity.goBack) {
            SettingsActivity.goBack = false

            this.finish()
        }
        if (SettingsActivity.isLanguageChanged || SettingsActivity.isFontChanged) {
            SettingsActivity.isLanguageChanged = false
            SettingsActivity.isFontChanged = false
            recreate()
        }
        super.onResume()
        Log.d("Debug", "MainActivity onResume")

        /*when (BaseActivity.dFontSize) {
            "big" -> {
                settingsB.textSize = resources.getDimension(R.dimen.text_big)
                createNewB.textSize = resources.getDimension(R.dimen.text_big)
            }
            "small" -> {
                settingsB.textSize = resources.getDimension(R.dimen.text_small)
                createNewB.textSize = resources.getDimension(R.dimen.text_small)
            }
            "normal" -> {
                settingsB.textSize = resources.getDimension(R.dimen.text_normal)
                createNewB.textSize = resources.getDimension(R.dimen.text_normal)
            }
        }*/
        iniButtons()
    }

    private fun iniButtons() {
        workoutListLayout.removeAllViews()

        val listOfNames = PrefUtil.getFileNames(this)

        var temp = listOfNames
        while (temp != "") {
            val fileName = temp.substringBefore(',')
            temp = temp.replace(fileName, "")
            if (temp != "" && temp[0] == ',') {
                temp = temp.substringAfter(", ")
            }
            val title = fileName.substringAfter("ppo1.").substringBeforeLast(')')

            val button = Button(this)
            button.movementMethod = ScrollingMovementMethod.getInstance()
            button.text = title
            button.minHeight = 300
            when (BaseActivity.dFontSize) {
                "big" -> button.textSize = resources.getDimension(R.dimen.text_big)
                "small" -> button.textSize = resources.getDimension(R.dimen.text_small)
                "normal" -> button.textSize = resources.getDimension(R.dimen.text_normal)
            }
            // button.textSize = resources.getDimension(R.dimen.text_normal)
            button.verticalScrollbarPosition
            workoutListLayout.addView(button)
            button.setOnClickListener {
                val fileName = "(com.example.ppo1.${button.text})"
                val intent = Intent(this, WorkoutActivity::class.java)
                intent.putExtra("FILE_NAME", fileName)
                this.startActivity(intent)
            }
        }
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

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }
}
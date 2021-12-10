package com.example.ppo1

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.ppo1.AppConstants.Companion.FILE_NAME
import com.example.ppo1.util.PrefUtil
import com.example.ppo1.util.convertMinutesToSeconds
import com.example.ppo1.util.convertSecondsToMinutes
import com.example.ppo1.util.getTimeFromStr
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.activity_workout.coolDownIntervalMinusB
import kotlinx.android.synthetic.main.activity_workout.coolDownIntervalPlusB
import kotlinx.android.synthetic.main.activity_workout.coolDownIntervalTV
import kotlinx.android.synthetic.main.activity_workout.restIntervalMinusB
import kotlinx.android.synthetic.main.activity_workout.restIntervalPlusB
import kotlinx.android.synthetic.main.activity_workout.restIntervalTV
import kotlinx.android.synthetic.main.activity_workout.saveB
import kotlinx.android.synthetic.main.activity_workout.setNumberMinusB
import kotlinx.android.synthetic.main.activity_workout.setNumberPlusB
import kotlinx.android.synthetic.main.activity_workout.setNumberTV
import kotlinx.android.synthetic.main.activity_workout.startB
import kotlinx.android.synthetic.main.activity_workout.titleET
import kotlinx.android.synthetic.main.activity_workout.warmUpIntervalMinusB
import kotlinx.android.synthetic.main.activity_workout.warmUpIntervalPlusB
import kotlinx.android.synthetic.main.activity_workout.warmUpIntervalTV
import kotlinx.android.synthetic.main.activity_workout.workIntervalMinusB
import kotlinx.android.synthetic.main.activity_workout.workIntervalPlusB
import kotlinx.android.synthetic.main.activity_workout.workIntervalTV

class WorkoutActivity : BaseActivity() {

    private var previousTitle = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        TimerActivity.isPaused = true

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // var fileName = intent.getStringExtra("FILE_NAME").toString()
        var fileName = savedInstanceState?.getSerializable("FILE_NAME")?.toString()
            ?: if (intent.extras == null) {
                ""
            } else {
                intent.extras!!.getString("FILE_NAME").toString()
            }

        if (fileName != "")
            getWorkoutFromFile(fileName)

        startB.setOnClickListener {
            val title = titleET.text.toString()
            val setNumber: Int = setNumberTV.text.toString().toInt()
            val workSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(workIntervalTV.text.toString()).first,
                getTimeFromStr(workIntervalTV.text.toString()).second
            )
            val restSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(restIntervalTV.text.toString()).first,
                getTimeFromStr(restIntervalTV.text.toString()).second
            )
            val warmUpSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(warmUpIntervalTV.text.toString()).first,
                getTimeFromStr(warmUpIntervalTV.text.toString()).second
            )
            val coolDownSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(coolDownIntervalTV.text.toString()).first,
                getTimeFromStr(coolDownIntervalTV.text.toString()).second
            )

            var colour: String = ""
            when {
                redRadioB.isChecked -> colour = "red"
                greenRadioB.isChecked -> colour = "green"
                blueRadioB.isChecked -> colour = "blue"
            }

            if (setNumber != 0 && workSeconds != 0 && restSeconds != 0 &&
                warmUpSeconds != 0 && coolDownSeconds != 0 && title != "" &&
                !title.contains(',') && colour != ""
            ) {

                PrefUtil.setIniSetNumber(setNumber, this)
                PrefUtil.setCurrentSetNumber(0, this)
                PrefUtil.setIniWorkSeconds(workSeconds + 1, this)
                PrefUtil.setIniRestSeconds(restSeconds + 1, this)
                PrefUtil.setIniWarmUpSeconds(warmUpSeconds + 1, this)
                PrefUtil.setIniCoolDownSeconds(coolDownSeconds + 1, this)
                PrefUtil.setCurrentStepNumber(TimerActivity.TimerStep.WarmUp, this)
                PrefUtil.setCurrentTime(warmUpSeconds + 1, this)
                PrefUtil.setTimerState(false, this)

                val intent = Intent(this, TimerActivity::class.java)
                this.startActivity(intent)
            }
        }

        deleteB.setOnClickListener {
            val fileName = "(com.example.ppo1.$previousTitle)"
            var listOfNames = PrefUtil.getFileNames(this)

            if (listOfNames.contains(fileName)) {
                listOfNames = listOfNames.replace(fileName, "")
                listOfNames = listOfNames.replace(", ,", ",")
            }

            PrefUtil.setFileNames(listOfNames, this)
            PrefUtil.removeFile(fileName, this)
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(resources.getString(R.string.msg_deleted))
                .setPositiveButton("ОК") { dialog, _ ->
                    dialog.cancel()
                }
            dialog.create()
            dialog.show()
            finish()
        }

        saveB.setOnClickListener {
            val title = titleET.text.toString()
            val setNumber: Int = setNumberTV.text.toString().toInt()
            val workSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(workIntervalTV.text.toString()).first,
                getTimeFromStr(workIntervalTV.text.toString()).second
            )
            val restSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(restIntervalTV.text.toString()).first,
                getTimeFromStr(restIntervalTV.text.toString()).second
            )
            val warmUpSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(warmUpIntervalTV.text.toString()).first,
                getTimeFromStr(warmUpIntervalTV.text.toString()).second
            )
            val coolDownSeconds: Int = convertMinutesToSeconds(
                getTimeFromStr(coolDownIntervalTV.text.toString()).first,
                getTimeFromStr(coolDownIntervalTV.text.toString()).second
            )

            var colour: String = ""
            when {
                redRadioB.isChecked -> colour = "red"
                greenRadioB.isChecked -> colour = "green"
                blueRadioB.isChecked -> colour = "blue"
            }

            if (setNumber != 0 && workSeconds != 0 && restSeconds != 0 &&
                warmUpSeconds != 0 && coolDownSeconds != 0 && title != "" &&
                !title.contains(',') && colour != ""
            ) {
                var listOfNames = PrefUtil.getFileNames(this)

                var fileName: String
                if (title != previousTitle) {
                    fileName = "(com.example.ppo1.$previousTitle)"

                    if (listOfNames.contains(fileName)) {
                        listOfNames = listOfNames.replace(fileName, "")
                        listOfNames = listOfNames.replace(", ,", ",")
                    }

                    PrefUtil.setFileNames(listOfNames, this)
                    PrefUtil.removeFile(fileName, this)
                }

                fileName = "(com.example.ppo1.$title)"

                if (!listOfNames.contains(fileName)) {
                    listOfNames += if (listOfNames != "") {
                        ", $fileName"
                    } else {
                        fileName
                    }
                    PrefUtil.setFileNames(listOfNames, this)


                    val workout = Workout(
                        title, setNumber, workSeconds,
                        restSeconds, warmUpSeconds, coolDownSeconds, colour
                    )

                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    PrefUtil.setFile(gson.toJson(workout), this, fileName)

                    previousTitle = title

                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(resources.getString(R.string.msg_saved))
                        .setPositiveButton("ОК") { dialog, _ ->
                            dialog.cancel()
                        }
                    dialog.create()
                    dialog.show()
                } else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(resources.getString(R.string.msg_title_already_exist))
                        .setPositiveButton("ОК") { dialog, _ ->
                            dialog.cancel()
                        }
                    dialog.create()
                    dialog.show()
                }
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage(resources.getString(R.string.msg_not_saved))
                    .setPositiveButton("ОК") { dialog, _ ->
                        dialog.cancel()
                    }
                dialog.create()
                dialog.show()
            }
        }

        iniButtonListener()
    }

    override fun onStart() {
        Log.d("Debug", "WorkoutActivity onResume before if")

        if (!TimerActivity.isPaused) {
            val intent = Intent(this, TimerActivity::class.java)
            this.startActivity(intent)
            //finish()
        }
        super.onStart()

        Log.d("Debug", "WorkoutActivity onResume after if")
    }

    private fun getWorkoutFromFile(fileName: String) {

        val builder = GsonBuilder()
        val gson: Gson = builder.create()
        val workout = gson.fromJson(PrefUtil.getFile(this, fileName), Workout::class.java)

        titleET.setText(workout.title)
        setNumberTV.text = workout.iniSetNumber.toString()
        workIntervalTV.text = getTimeInFormat(workout.iniWorkSeconds)
        restIntervalTV.text = getTimeInFormat(workout.iniRestSeconds)
        warmUpIntervalTV.text = getTimeInFormat(workout.iniWarmUpSeconds)
        coolDownIntervalTV.text = getTimeInFormat(workout.iniCoolDownSeconds)
        previousTitle = workout.title

        when (workout.colour) {
            "red" -> redRadioB.isChecked = true
            "green" -> greenRadioB.isChecked = true
            "blue" -> blueRadioB.isChecked = true
        }
    }

    private fun iniButtonListener() {
        setNumberMinusB.setOnClickListener {
            plusOrMinus1(setNumberTV, false)
        }
        setNumberPlusB.setOnClickListener {
            plusOrMinus1(setNumberTV)
        }
        workIntervalPlusB.setOnClickListener {
            plusNumber(workIntervalTV)
        }
        workIntervalMinusB.setOnClickListener {
            minusNumber(workIntervalTV)
        }
        restIntervalPlusB.setOnClickListener {
            plusNumber(restIntervalTV)
        }
        restIntervalMinusB.setOnClickListener {
            minusNumber(restIntervalTV)
        }
        warmUpIntervalPlusB.setOnClickListener {
            plusNumber(warmUpIntervalTV, number = 5)
        }
        warmUpIntervalMinusB.setOnClickListener {
            minusNumber(warmUpIntervalTV, number = 5)
        }
        coolDownIntervalPlusB.setOnClickListener {
            plusNumber(coolDownIntervalTV, number = 5)
        }
        coolDownIntervalMinusB.setOnClickListener {
            minusNumber(coolDownIntervalTV, number = 5)
        }

        setButtonLongClick(restIntervalPlusB, restIntervalTV)
        setButtonLongClick(workIntervalPlusB, workIntervalTV)
        setButtonLongClick(warmUpIntervalPlusB, warmUpIntervalTV)
        setButtonLongClick(coolDownIntervalPlusB, coolDownIntervalTV)
        setButtonLongClick(restIntervalMinusB, restIntervalTV, add = false)
        setButtonLongClick(workIntervalMinusB, workIntervalTV, add = false)
        setButtonLongClick(warmUpIntervalMinusB, warmUpIntervalTV, add = false)
        setButtonLongClick(coolDownIntervalMinusB, coolDownIntervalTV, add = false)
        setButtonLongClick(setNumberMinusB, setNumberTV, add = false, time = false)
        setButtonLongClick(setNumberPlusB, setNumberTV, add = true, time = false)
    }

    private fun plusNumber(textViewTime: TextView, number: Int = 10) {
        var currentTime = textViewTime.text.toString()
        var seconds = getTimeFromStr(currentTime).second
        var minutes = getTimeFromStr(currentTime).first
        if (seconds < 60 - number) {
            seconds += number
        } else if (seconds == 60 - number) {
            seconds = 0
            minutes += 1
        }
        currentTime = String.format(AppConstants.FORMAT, minutes) + ":" + String.format(
            AppConstants.FORMAT,
            seconds
        )
        textViewTime.text = currentTime
    }

    private fun minusNumber(textViewTime: TextView, number: Int = 10) {
        var currentTime = textViewTime.text.toString()
        var seconds = getTimeFromStr(currentTime).second
        var minutes = getTimeFromStr(currentTime).first
        if (seconds > 0) {
            seconds -= number
        } else if (seconds == 0) {
            if (minutes != 0) {
                seconds = 60 - number
                minutes -= 1
            } else {
                seconds = 0
                minutes = 0
            }
        }
        currentTime = String.format(AppConstants.FORMAT, minutes) + ":" + String.format(
            AppConstants.FORMAT,
            seconds
        )
        textViewTime.text = currentTime
    }

    private fun plusOrMinus1(textViewSet: TextView, add: Boolean = true) {
        var currentSetNumber = textViewSet.text.toString().toInt()
        if (add) {
            currentSetNumber += 1
        } else if (currentSetNumber != 0 && !add) {
            currentSetNumber -= 1
        } else if (currentSetNumber == 0 && !add) {
            currentSetNumber = 0
        }
        textViewSet.text = currentSetNumber.toString()
    }

    private fun setButtonLongClick(
        button: ImageButton,
        textView: TextView,
        add: Boolean = true,
        time: Boolean = true
    ) {
        button.setOnLongClickListener(object : View.OnLongClickListener {
            private val mHandler: Handler = Handler()
            private val incrementRunnable: Runnable = object : Runnable {
                override fun run() {
                    mHandler.removeCallbacks(this)
                    if (button.isPressed) {
                        if (time) {
                            if (add) {
                                plusNumber(textView)
                            } else {
                                minusNumber(textView)
                            }
                        } else {
                            plusOrMinus1(textView, add)
                        }
                        mHandler.postDelayed(this, 100)
                    }
                }
            }

            override fun onLongClick(view: View): Boolean {
                mHandler.postDelayed(incrementRunnable, 0)
                return true
            }
        })
    }

    private fun getTimeInFormat(sec: Int): String {
        val (minutes, seconds) = convertSecondsToMinutes(sec)
        return String.format(
            AppConstants.FORMAT,
            minutes
        ) + ":" + String.format(AppConstants.FORMAT, seconds)
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
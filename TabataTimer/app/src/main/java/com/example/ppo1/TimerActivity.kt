package com.example.ppo1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.ppo1.AppConstants.Companion.FORMAT
import com.example.ppo1.util.*
import kotlinx.android.synthetic.main.activity_timer.*
import java.util.*
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.util.Log


class TimerActivity : BaseActivity() {

    companion object {
        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000

        var isPaused: Boolean = false
    }

    enum class TimerStep {
        WarmUp, Work, Rest, CoolDown, Done
    }

    private var iniSetNumber: Int = 0
    private var iniWorkSeconds: Int = 0
    private var iniRestSeconds: Int = 0
    private var iniWarmUpSeconds: Int = 0
    private var iniCoolDownSeconds: Int = 0
    private var currentSetNumber: Int = 0
    private var currentStep = TimerStep.WarmUp
    private var currentTime: Int = 0  // seconds remaining
    private var timer: CountDownTimer? = null


    private lateinit var mpRest: MediaPlayer
    private lateinit var mpWork: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mpRest = MediaPlayer.create(this, R.raw.beep)
        mpWork = MediaPlayer.create(this, R.raw.boop)

        iniActionButtons()
    }

    override fun onResume() {
        super.onResume()

        initTimer()
        iniPhasesList()

        stopService(Intent(this, TimerService::class.java))
        removeAlarm(this)
    }

    override fun onPause() {
        super.onPause()

        if (!isPaused) {
            cancelTimer()
            startService(Intent(this, TimerService::class.java))
            val wakeUpTime = setAlarm(this, nowSeconds, currentTime.toLong())
        }

        setTimerData()

        Log.d("Debug", "TimerActivity onPause")
    }

    private fun initTimer() {
        getTimerData()

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            currentTime -= nowSeconds.toInt() - alarmSetTime.toInt()

        when (currentStep) {
            TimerStep.WarmUp -> iniGetReady(currentTime)
            TimerStep.Work -> iniWorkout(currentTime)
            TimerStep.Rest -> iniRest(currentTime)
            TimerStep.CoolDown -> iniCoolDown(currentTime)
            TimerStep.Done -> iniDone()
        }
        if (isPaused) {
            cancelTimer()
        }
    }

    private fun setTimerData() {
        PrefUtil.setIniSetNumber(iniSetNumber, this)
        PrefUtil.setCurrentSetNumber(currentSetNumber, this)
        PrefUtil.setIniWorkSeconds(iniWorkSeconds, this)
        PrefUtil.setIniRestSeconds(iniRestSeconds, this)
        PrefUtil.setIniWarmUpSeconds(iniWarmUpSeconds, this)
        PrefUtil.setIniCoolDownSeconds(iniCoolDownSeconds, this)
        PrefUtil.setCurrentStepNumber(currentStep, this)
        PrefUtil.setCurrentTime(currentTime, this)
        PrefUtil.setTimerState(isPaused, this)
    }

    private fun getTimerData() {
        iniSetNumber = PrefUtil.getIniSetNumber(this)
        currentSetNumber = PrefUtil.getCurrentSetNumber(this)
        iniWorkSeconds = PrefUtil.getIniWorkSeconds(this)
        iniRestSeconds = PrefUtil.getIniRestSeconds(this)
        iniWarmUpSeconds = PrefUtil.getIniWarmUpSeconds(this)
        iniCoolDownSeconds = PrefUtil.getIniCoolDownSeconds(this)
        currentStep = PrefUtil.getCurrentStepNumber(this)
        currentTime = PrefUtil.getCurrentTime(this)
        isPaused = PrefUtil.getTimerState(this)
    }

    private fun iniActionButtons() {
        stopB.setOnClickListener {
            isPaused = true
            cancelTimer()
            this.finish()
        }
        replayB.setOnClickListener {
            cancelTimer()
            currentSetNumber = 0
            iniGetReady()
            isPaused = false
        }
        playPauseB.setOnClickListener {
            if (!isPaused) {
                cancelTimer()
                playPauseB.setImageResource(R.drawable.ic_play_arrow_24px)
                isPaused = true
                setTimerData()
            } else {
                if (currentStep != TimerStep.Done) {

                    playPauseB.setImageResource(R.drawable.ic_pause_24px)
                    startTimer(currentTime)
                    isPaused = false
                }
            }
        }
        nextB.setOnClickListener {
            cancelTimer()
            if (currentStep == TimerStep.WarmUp || currentStep == TimerStep.Rest) {
                iniWorkout()
            } else if (currentStep == TimerStep.Work) {
                if (currentSetNumber == iniSetNumber)
                    iniCoolDown()
                else
                    iniRest()
            } else if (currentStep == TimerStep.CoolDown) {
                iniDone()
            }
            if (isPaused) {
                cancelTimer()
            }
        }

        beforeB.setOnClickListener {
            cancelTimer()
            if (currentStep == TimerStep.WarmUp) {
                currentSetNumber = 0
                iniGetReady()
                isPaused = false
            } else if (currentSetNumber == 1 && currentStep == TimerStep.Work) {
                currentSetNumber = 0
                iniGetReady()
            } else if (currentSetNumber == iniSetNumber + 1 && currentStep == TimerStep.Done) {
                currentSetNumber -= 1
                stepTV.isVisible = true
                stepCountTV.isVisible = true
                playPauseB.setImageResource(R.drawable.ic_pause_24px)
                isPaused = false
                iniCoolDown()
            } else {
                currentSetNumber -= 1
                when (currentStep) {
                    TimerStep.Rest -> {
                        iniWorkout()
                    }
                    TimerStep.Work -> {
                        iniRest()
                    }
                    TimerStep.CoolDown -> {
                        iniWorkout()
                    }
                    else -> {
                        finish()
                    }
                }
            }
            if (isPaused) {
                cancelTimer()
            }
        }
    }

    private fun setTimeString() {
        val seconds = currentTime % 60
        val minutes = (currentTime - seconds) / 60

        val temp = String.format(FORMAT, minutes) + ":" + String.format(FORMAT, seconds)
        timeTV.text = temp
    }

    private fun iniGetReady(secondsRemaining: Int = -1) {
        currentStep = TimerStep.WarmUp // 0
        playPauseB.setImageResource(R.drawable.ic_pause_24px)
        stepTV.isVisible = true
        stepCountTV.isVisible = true
        constraintLayout.setBackgroundResource(R.drawable.green_gradient)
        val temp = "${resources.getString(R.string.upper_set)} $currentSetNumber"
        stepCountTV.text = temp
        stepTV.text = resources.getString(R.string.upper_get_ready)
        if (secondsRemaining == -1)
            startTimer(iniWarmUpSeconds)
        else
            startTimer(secondsRemaining)
        currentSetNumber += 1

        setTimeString()
    }

    private fun iniWorkout(secondsRemaining: Int = -1) {
        currentStep = TimerStep.Work // 1
        constraintLayout.setBackgroundResource(R.drawable.pink_gradient)
        var temp = "${resources.getString(R.string.upper_set)} $currentSetNumber"
        stepCountTV.text = temp
        stepTV.text = resources.getString(R.string.upper_work_it)

        if (secondsRemaining == -1)
            startTimer(iniWorkSeconds)
        else
            startTimer(secondsRemaining)

        setTimeString()
    }

    private fun iniRest(secondsRemaining: Int = -1) {
        currentStep = TimerStep.Rest // 2
        constraintLayout.setBackgroundResource(R.drawable.blue_gradient)
        var temp = "${resources.getString(R.string.upper_set)} $currentSetNumber"
        stepCountTV.text = temp
        stepTV.text = resources.getString(R.string.upper_rest_now)
        if (secondsRemaining == -1)
            startTimer(iniRestSeconds)
        else
            startTimer(secondsRemaining)
        currentSetNumber += 1

        setTimeString()
    }

    private fun iniCoolDown(secondsRemaining: Int = -1) {
        currentStep = TimerStep.CoolDown // 3
        constraintLayout.setBackgroundResource(R.drawable.green_gradient)
        var temp = "${resources.getString(R.string.upper_set)} $currentSetNumber"
        stepCountTV.text = temp
        stepTV.text = resources.getString(R.string.upper_cool_down)

        if (secondsRemaining == -1)
            startTimer(iniCoolDownSeconds)
        else
            startTimer(secondsRemaining)
        currentSetNumber += 1

        setTimeString()
    }

    private fun iniDone() {
        currentStep = TimerStep.Done // -1
        stepTV.isVisible = false
        stepCountTV.isVisible = false
        timeTV.text = resources.getString(R.string.upper_done)
        playPauseB.setImageResource(R.drawable.ic_play_arrow_24px)
        isPaused = true
    }

    private fun iniPhasesList() {
        phasesListLayout.removeAllViews()

        val phases: Array<String> = Array(iniSetNumber * 2 + 2) { "" }
        phases[0] = "1. ${getString(R.string.upper_get_ready)}: ${iniWarmUpSeconds - 1}"
        var i = 1
        while (i < iniSetNumber * 2) {
            if (i % 2 != 0)
                phases[i] = "${i + 1}. ${getString(R.string.upper_work_it)}: ${iniWorkSeconds - 1}"
            else
                phases[i] = "${i + 1}. ${getString(R.string.upper_rest_now)}: ${iniRestSeconds - 1}"
            i++
        }
        phases[i] = "${i + 1}. ${getString(R.string.upper_cool_down)}: ${iniCoolDownSeconds - 1}"
        i++
        phases[i] = "${i + 1}. ${getString(R.string.upper_done)}"

        //phasesTitle.movementMethod = ScrollingMovementMethod.getInstance()
        for (phase in phases) {
            val textView = TextView(this)
            textView.movementMethod = ScrollingMovementMethod.getInstance()
            textView.text = phase
            textView.textSize = 30F
            textView.verticalScrollbarPosition
            textView.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.whiteTransparent50
                )
            )
            phasesListLayout.addView(textView)
        }
    }

    private fun onTimerTick(textViewTime: TextView) {

        if (currentStep == TimerStep.Work)
            mpWork.start()
        else
            mpRest.start()

        var seconds = currentTime % 60
        var minutes = (currentTime - seconds) / 60
        if (minutes != 0) {
            if (seconds == 0) {
                seconds = 59
                minutes -= 1
            } else {
                seconds -= 1
            }
        } else {
            if (seconds in 1..4) {
                seconds -= 1
                var vibrationTime = 100L
                if (seconds == 0) {
                    seconds = 0
                    minutes = 0
                    vibrationTime = 500L
                }
                val vib = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                            as VibratorManager
                    vibratorManager.defaultVibrator
                } else {
                    getSystemService(VIBRATOR_SERVICE) as Vibrator
                }

                val canVibrate: Boolean = vib.hasVibrator()

                if (canVibrate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // API 26
                        vib.vibrate(
                            VibrationEffect.createOneShot(
                                vibrationTime,
                                VibrationEffect.DEFAULT_AMPLITUDE
                            )
                        )
                    } else {
                        // This method was deprecated in API level 26
                        vib.vibrate(vibrationTime)
                    }
                }
            } else {
                seconds -= 1
            }
        }
        currentTime = minutes * 60 + seconds
        val temp = String.format(FORMAT, minutes) + ":" + String.format(FORMAT, seconds)
        textViewTime.text = temp
    }

    private fun startTimer(sec: Int) {
        currentTime = sec

        timer = object : CountDownTimer((sec * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTimerTick(timeTV)
            }

            override fun onFinish() {
                if (currentSetNumber != iniSetNumber + 1) {
                    if (currentStep == TimerStep.WarmUp || currentStep == TimerStep.Rest) {
                        iniWorkout()
                    } else if (currentStep == TimerStep.Work) {
                        if (currentSetNumber == iniSetNumber)
                            iniCoolDown()
                        else
                            iniRest()
                    } else if (currentStep == TimerStep.CoolDown) {
                        iniDone()
                    } else {
                        finish()
                    }
                } else {
                    iniDone()
                }
            }
        }
        (timer as CountDownTimer).start()
    }

    private fun cancelTimer() {
        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTimer()
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
package com.example.ppo1

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ppo1.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    private var iniSetNumber: Int = 0
    private var iniWorkSeconds: Int = 0
    private var iniRestSeconds: Int = 0
    private var iniWarmUpSeconds: Int = 0
    private var iniCoolDownSeconds: Int = 0
    private var currentSetNumber: Int = 0
    private var currentStep = TimerActivity.TimerStep.WarmUp
    private var currentTime: Int = 0  // seconds remaining
    private var isPaused: Boolean = false

    override fun onReceive(context: Context, intent: Intent) {
        /*NotificationUtil.showTimerExpired(context)*/

        getTimerData(context)

        if (currentStep == TimerActivity.TimerStep.WarmUp ||
            currentStep == TimerActivity.TimerStep.Rest
        ) {
            currentStep = TimerActivity.TimerStep.Work
            currentTime = iniWorkSeconds
        } else if (currentStep == TimerActivity.TimerStep.Work) {
             if (currentSetNumber == iniSetNumber) {
                 currentStep = TimerActivity.TimerStep.CoolDown
                 currentTime = iniCoolDownSeconds
             }
            else {
                 currentStep = TimerActivity.TimerStep.Rest
                 currentSetNumber += 1
                 currentTime = iniRestSeconds
             }
        } else if (currentStep == TimerActivity.TimerStep.CoolDown) {
            currentStep = TimerActivity.TimerStep.Done
            currentTime = 0
        }

        setTimerData(context)

        val wakeUpTime =
            TimerActivity.setAlarm(context, TimerActivity.nowSeconds, currentTime.toLong())

        /*PrefUtil.setTimerState(true, context)
        PrefUtil.setAlarmSetTime(0, context)*/
    }

    private fun setTimerData(context: Context) {
        PrefUtil.setIniSetNumber(iniSetNumber, context)
        PrefUtil.setCurrentSetNumber(currentSetNumber, context)
        PrefUtil.setIniWorkSeconds(iniWorkSeconds, context)
        PrefUtil.setIniRestSeconds(iniRestSeconds, context)
        PrefUtil.setIniWarmUpSeconds(iniWarmUpSeconds, context)
        PrefUtil.setIniCoolDownSeconds(iniCoolDownSeconds, context)
        PrefUtil.setCurrentStepNumber(currentStep, context)
        PrefUtil.setCurrentTime(currentTime, context)
        PrefUtil.setTimerState(isPaused, context)
    }

    private fun getTimerData(context: Context) {
        iniSetNumber = PrefUtil.getIniSetNumber(context)
        currentSetNumber = PrefUtil.getCurrentSetNumber(context)
        iniWorkSeconds = PrefUtil.getIniWorkSeconds(context)
        iniRestSeconds = PrefUtil.getIniRestSeconds(context)
        iniWarmUpSeconds = PrefUtil.getIniWarmUpSeconds(context)
        iniCoolDownSeconds = PrefUtil.getIniCoolDownSeconds(context)
        currentStep = PrefUtil.getCurrentStepNumber(context)
        currentTime = PrefUtil.getCurrentTime(context)
        isPaused = PrefUtil.getTimerState(context)
    }
}
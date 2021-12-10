package com.example.ppo1.util

import android.content.SharedPreferences
import android.text.method.TextKeyListener.clear
import androidx.preference.PreferenceManager
import com.example.ppo1.TimerActivity
import android.content.Context as ContentContext

class PrefUtil {
    companion object{

        private const val TIMER_LENGTH_ID = "com.example.ppo1.timer_length"
        fun getTimerLength(context: ContentContext): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 10)
        }

        private const val SECONDS_REMAINING_ID = "com.example.ppo1.seconds_remaining"

        fun getSecondsRemaining(context: ContentContext): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(seconds: Long, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }

        private const val ALARM_SET_TIME_ID = "com.example.ppo1.background_time"

        fun getAlarmSetTime(context: ContentContext): Long{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: ContentContext) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }

        private const val INITIAL_SET_NUMBER_ID = "com.example.ppo1.initial_set_number"

        fun getIniSetNumber(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(INITIAL_SET_NUMBER_ID, 0)
        }

        fun setIniSetNumber(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(INITIAL_SET_NUMBER_ID, number)
            editor.apply()
        }

        private const val CURRENT_SET_NUMBER_ID = "com.example.ppo1.current_set_number"

        fun getCurrentSetNumber(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(CURRENT_SET_NUMBER_ID, 0)
        }

        fun setCurrentSetNumber(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(CURRENT_SET_NUMBER_ID, number)
            editor.apply()
        }

        private const val INI_WORK_SECONDS_ID = "com.example.ppo1.ini_work_seconds"

        fun getIniWorkSeconds(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(INI_WORK_SECONDS_ID, 0)
        }

        fun setIniWorkSeconds(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(INI_WORK_SECONDS_ID, number)
            editor.apply()
        }

        private const val INI_REST_SECONDS_ID = "com.example.ppo1.ini_rest_seconds"

        fun getIniRestSeconds(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(INI_REST_SECONDS_ID, 0)
        }

        fun setIniRestSeconds(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(INI_REST_SECONDS_ID, number)
            editor.apply()
        }

        private const val INI_WARM_UP_SECONDS_ID = "com.example.ppo1.ini_warm_up_seconds"

        fun getIniWarmUpSeconds(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(INI_WARM_UP_SECONDS_ID, 0)
        }

        fun setIniWarmUpSeconds(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(INI_WARM_UP_SECONDS_ID, number)
            editor.apply()
        }

        private const val INI_COOL_DOWN_SECONDS_ID = "com.example.ppo1.ini_cool_down_seconds"

        fun getIniCoolDownSeconds(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(INI_COOL_DOWN_SECONDS_ID, 0)
        }

        fun setIniCoolDownSeconds(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(INI_COOL_DOWN_SECONDS_ID, number)
            editor.apply()
        }

        private const val CURRENT_STEP_NUMBER_ID = "com.example.ppo1.current_step_number"

        fun getCurrentStepNumber(context: ContentContext): TimerActivity.TimerStep {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(CURRENT_STEP_NUMBER_ID, 0)
            return TimerActivity.TimerStep.values()[ordinal]
        }

        fun setCurrentStepNumber(number: TimerActivity.TimerStep, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = number.ordinal
            editor.putInt(CURRENT_STEP_NUMBER_ID, ordinal)
            editor.apply()
        }

        private const val CURRENT_TIME_ID = "com.example.ppo1.current_time"

        fun getCurrentTime(context: ContentContext): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(CURRENT_TIME_ID, 0)
        }

        fun setCurrentTime(number: Int, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(CURRENT_TIME_ID, number)
            editor.apply()
        }
        
        private const val CURRENT_TIMER_STATE_ID = "com.example.ppo1.current_timer_state"

        fun getTimerState(context: ContentContext): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(CURRENT_TIMER_STATE_ID, 0) != 0
        }

        fun setTimerState(state: Boolean, context: ContentContext){
            val number = if (state) 1
            else 0
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(CURRENT_TIMER_STATE_ID, number)
            editor.apply()
        }

        private const val FILE_NAMES_ID = "com.example.ppo1.file_names"

        fun getFileNames(context: ContentContext): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(FILE_NAMES_ID, "").toString()
        }

        fun setFileNames(names: String, context: ContentContext){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(FILE_NAMES_ID, names)
            editor.apply()
        }

        fun getFile(context: ContentContext, filename: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(filename, "").toString()
        }

        fun setFile(text: String, context: ContentContext, filename: String){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(filename, text)
            editor.apply()
        }

        fun removeFile(filename: String, context: ContentContext) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.remove(filename)
            editor.apply()
        }

        fun removeData(context: android.content.Context){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.clear()
            editor.apply()
        }
    }
}
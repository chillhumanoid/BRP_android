package com.theunquenchedservant.granthornersbiblereadingsystem.service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.theunquenchedservant.granthornersbiblereadingsystem.MainActivity
import com.theunquenchedservant.granthornersbiblereadingsystem.service.AlarmCreator.createAlarm
import com.theunquenchedservant.granthornersbiblereadingsystem.utilities.Log.traceLog

class ServiceStarted  : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        traceLog(file="ServiceStarted.kt", function="onReceive")
        if (intent?.action == "android.intent.action.BOOT_COMPLETED" || intent?.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            createAlarm(alarmType="dailyCheck")
            createAlarm(alarmType="daily")
            createAlarm(alarmType="remind")
        }
    }
}
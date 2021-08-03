package com.example.medilista.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.medilista.alarm.AlarmReceiver.Companion.schedulePushNotifications

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            schedulePushNotifications(context)
        }
    }
}
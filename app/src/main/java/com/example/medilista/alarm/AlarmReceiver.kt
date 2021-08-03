package com.example.medilista.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.medilista.R
import com.example.medilista.sendNotification
import java.util.*


class AlarmReceiver: BroadcastReceiver() {

    companion object {

        fun schedulePushNotifications(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmPendingIntent by lazy {
                val intent = Intent(context, AlarmReceiver::class.java)
                PendingIntent.getBroadcast(context, 0, intent, 0)
            }
            val HOUR_TO_SHOW_PUSH = 15
            val MIN_TO_SHOW_PUSH = 37
            val calendar = GregorianCalendar.getInstance().apply {
                if (get(Calendar.HOUR_OF_DAY) >= HOUR_TO_SHOW_PUSH) {
                    if (get(Calendar.MINUTE) >= MIN_TO_SHOW_PUSH) {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                }

                set(Calendar.HOUR_OF_DAY, HOUR_TO_SHOW_PUSH)
                set(Calendar.MINUTE, MIN_TO_SHOW_PUSH)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmPendingIntent
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
        ) as NotificationManager


        notificationManager.sendNotification(
                context.getText(R.string.test_notification).toString(),
                context
        )
    }
}
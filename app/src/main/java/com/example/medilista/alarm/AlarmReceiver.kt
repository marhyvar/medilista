package com.example.medilista.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_MESSAGE = "notification_message"

        fun schedulePushNotifications(context: Context) {
            CoroutineScope(Dispatchers.Default).launch {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val dataSource = MedicineDatabase.getInstance(context).medicineDao

                val dosages = dataSource.getDosageList()
                Log.i("äää", dosages.size.toString())

                dosages.forEach { dosage ->
                    val alarmPendingIntent by lazy {
                        val intent = Intent(context, AlarmReceiver::class.java)
                        intent.putExtra(NOTIFICATION_MESSAGE, "Ota lääkkees")
                        PendingIntent.getBroadcast(context, 0, intent, 0)
                    }
                    Log.i("äää", dosage.amount.toString())
                    val HOUR_TO_SHOW_PUSH = dosage.timeValueHours
                    val MIN_TO_SHOW_PUSH = dosage.timeValueMinutes
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



        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
        ) as NotificationManager


        val message = intent.getStringExtra(NOTIFICATION_MESSAGE)
        if (message != null) {
            notificationManager.sendNotification(
                    message,
                    context
            )
        }
    }
}
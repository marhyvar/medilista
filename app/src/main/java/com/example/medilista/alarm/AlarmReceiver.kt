package com.example.medilista.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medilista.combineFormAmountAndTimes
import com.example.medilista.database.Dosage
import com.example.medilista.database.MedicineDatabase
import com.example.medilista.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AlarmReceiver: BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_MESSAGE = "notification_message"
        const val HOURS = "hours"
        const val MINUTES = "minutes"
        const val ID_NUMBER = "idNumber"

        fun scheduleNotification(context: Context, messageText: String, hour: Int, min: Int, idNumber: Int) {
            CoroutineScope(Dispatchers.Default).launch {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                //val randomInteger = (1..12).shuffled().first()
                //Log.i("ööö", randomInteger.toString())

                    val alarmPendingIntent by lazy {
                        val intent = Intent(context, AlarmReceiver::class.java)
                        intent.putExtra(NOTIFICATION_MESSAGE, messageText)
                        intent.putExtra(HOURS, hour)
                        intent.putExtra(MINUTES, min)
                        intent.putExtra(ID_NUMBER, idNumber.toInt())
                        PendingIntent.getBroadcast(context, idNumber.toInt(), intent, 0)
                    }
                    Log.i("ööö", messageText)
                    val HOUR_TO_SHOW_PUSH = hour
                    val MIN_TO_SHOW_PUSH = min
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

                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis,
                            //AlarmManager.INTERVAL_DAY,
                            alarmPendingIntent
                    )

            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
        ) as NotificationManager
        val message = intent.getStringExtra(NOTIFICATION_MESSAGE)
        val hours = intent.getStringExtra(HOURS)?.toInt()
        val minutes = intent.getStringExtra(MINUTES)?.toInt()
        val number = intent.getStringExtra(ID_NUMBER)?.toInt()

        try {

            if (message != null) {
                notificationManager.sendNotification(
                        message,
                        context
                )
            }
        }
        catch (e: Exception) {
            Log.i("ööö", e.toString())
        }
        finally {
            // schedule repeating alarm for next day
            if (message != null) {
                if (hours != null) {
                    if (minutes != null) {
                        if (number != null) {
                            scheduleNotification(context, message, hours, minutes, number)
                        }
                    }
                }
            }
        }
    }
}
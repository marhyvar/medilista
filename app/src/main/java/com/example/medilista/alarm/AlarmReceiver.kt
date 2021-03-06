package com.example.medilista.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.medilista.createNotificationText
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

                val alarmPendingIntent by lazy {
                    val intent = Intent(context, AlarmReceiver::class.java)
                    intent.putExtra(NOTIFICATION_MESSAGE, messageText)
                    intent.putExtra(HOURS, hour.toString())
                    intent.putExtra(MINUTES, min.toString())
                    intent.putExtra(ID_NUMBER, idNumber.toString())
                    PendingIntent.getBroadcast(context, idNumber, intent, FLAG_UPDATE_CURRENT)
                }
                Log.i("ööö", "scheduleNotification got: $messageText")
                val startTime = Calendar.getInstance()
                startTime[Calendar.HOUR_OF_DAY] = hour
                startTime[Calendar.MINUTE] = min

                val now = Calendar.getInstance()

                val alarmTime = if (now.before(startTime)) {
                    // set alarm for today
                    Log.i("ööö", "tänään")
                    startTime.timeInMillis
                } else {
                    // set alarm for tomorrow
                    Log.i("ööö", "huomenna")
                    startTime.add(Calendar.DATE, 1)
                    startTime.timeInMillis
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime,
                            alarmPendingIntent
                    )
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime,
                            alarmPendingIntent
                    )
                } else {
                    alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            alarmTime,
                            alarmPendingIntent
                    )
                }
            }
        }

        fun cancelAlarmNotification(context: Context, alarmId: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)
            Log.i("ööö", "cancelAlarmNotification done")
        }

        fun scheduleAllAlarmNotifications(context: Context) {
            CoroutineScope(Dispatchers.Default).launch {
                Log.i("ööö", "scheduleAllAlarmNotifications")
                val dataSource = MedicineDatabase.getInstance(context).medicineDao
                val medicines = dataSource.getAllMedicinesList()
                Log.i("ööö", medicines.size.toString())
                medicines.forEach { medicine ->
                    val med = medicine.Medicine
                    if (med.alarm) {
                        medicine.dosages.forEach { dos ->
                            val text = createNotificationText(
                                    med.medicineName, med.strength, med.form,
                                    dos.amount, dos.timeValueHours, dos.timeValueMinutes)

                            scheduleNotification(context, text, dos.timeValueHours,
                                    dos.timeValueMinutes, dos.dosageId.toInt())
                        }
                    }
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
        val hours = intent.getStringExtra(HOURS)?.toInt()
        val minutes = intent.getStringExtra(MINUTES)?.toInt()
        val number = intent.getStringExtra(ID_NUMBER)?.toInt()
        Log.i("ööö", message.toString())
        Log.i("ööö", hours.toString())
        Log.i("ööö", minutes.toString())
        Log.i("ööö", number.toString())

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
            Log.i("ööö", "finally block")
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

//Ivan Morgun https://en.proft.me/2017/05/7/scheduling-operations-alarmmanager-android/
// Desmond Lua https://code.luasoftware.com/tutorials/android/android-daily-repeating-alarm-at-specific-time-with-alarmmanager/
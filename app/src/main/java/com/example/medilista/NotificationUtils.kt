package com.example.medilista

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.core.app.NotificationCompat


private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0


fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )


    val notificationImage = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.baseline_delete_forever_24
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(notificationImage)
            .bigLargeIcon(null)

    //val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
    //val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
    //    applicationContext,
    //    REQUEST_CODE,
    //    snoozeIntent,
    //    FLAGS)


    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.med_notification_channel_id)
    )

            .setSmallIcon(R.drawable.baseline_delete_forever_24)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(messageBody)

            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)

            .setStyle(bigPicStyle)
            .setLargeIcon(notificationImage)

            //.addAction(
            //    R.drawable.baseline_delete_forever_24,
            //    applicationContext.getString(R.string.snooze),
            //    snoozePendingIntent
            //)

            .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}



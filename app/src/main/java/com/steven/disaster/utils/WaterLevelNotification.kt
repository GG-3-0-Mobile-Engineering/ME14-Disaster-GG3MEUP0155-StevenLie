package com.steven.disaster.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.steven.disaster.R
import com.steven.disaster.view.MainActivity

class WaterLevelNotification(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(message: String) {
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            WATER_LEVEL_NOTIFICATION_REQUEST_CODE,
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, WATER_LEVEL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.water_level_notification_title))
            .setContentText(message)
            .setContentIntent(activityPendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()

        notificationManager.notify(WATER_LEVEL_NOTIFICATION_REQUEST_CODE, notification)
    }

    companion object {
        const val WATER_LEVEL_CHANNEL_ID = "WATER_LEVEL_CHANNEL_ID"
        const val WATER_LEVEL_NOTIFICATION_REQUEST_CODE = 1
    }
}
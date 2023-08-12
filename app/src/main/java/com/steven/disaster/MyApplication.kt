package com.steven.disaster

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.steven.disaster.utils.WaterLevelNotification
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                WaterLevelNotification.WATER_LEVEL_CHANNEL_ID,
                getString(R.string.water_level_notification_title),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = getString(R.string.water_level_notification_description)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
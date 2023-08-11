package com.steven.disaster.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.steven.disaster.view.MainActivity

class WaterLevelNotificationWorker(
    private val context: Context,
    private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val waterLevelNotification = WaterLevelNotification(context)
            waterLevelNotification.showNotification(
                params.inputData.getString(MainActivity.WORK_MANAGER_DATA_KEY).toString()
            )

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
package com.steven.disaster.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.steven.disaster.view.MainActivity
import com.steven.disaster.viewmodel.MainViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WaterLevelWorker @AssistedInject constructor(
    @Assisted private val mainViewModel: MainViewModel,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
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
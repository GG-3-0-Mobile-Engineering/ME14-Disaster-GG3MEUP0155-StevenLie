package com.steven.disaster.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.steven.disaster.data.ApiService
import javax.inject.Inject

class WaterLevelWorkerFactory @Inject constructor(
    private val apiService: ApiService,
    private val waterLevelNotification: WaterLevelNotification
) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker =
        WaterLevelWorker(apiService, waterLevelNotification, appContext, workerParameters)
}
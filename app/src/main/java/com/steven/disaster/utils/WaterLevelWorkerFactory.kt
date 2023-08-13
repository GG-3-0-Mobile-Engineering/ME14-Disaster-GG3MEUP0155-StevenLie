package com.steven.disaster.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.steven.disaster.viewmodel.MainViewModel
import javax.inject.Inject

class WaterLevelWorkerFactory @Inject constructor(private val mainViewModel: MainViewModel) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = WaterLevelWorker(mainViewModel, appContext, workerParameters)
}
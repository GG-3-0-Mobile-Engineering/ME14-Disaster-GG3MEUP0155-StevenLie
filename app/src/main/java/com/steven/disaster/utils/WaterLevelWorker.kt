package com.steven.disaster.utils

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.steven.disaster.R
import com.steven.disaster.data.ApiService
import com.steven.disaster.data.response.TmaResponse
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@HiltWorker
class WaterLevelWorker @AssistedInject constructor(
    @Assisted private val apiService: ApiService,
    @Assisted private val waterLevelNotification: WaterLevelNotification,
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val client = apiService.getTma()
            var resultStatus: Result = Result.success()
            client.enqueue(object : Callback<TmaResponse> {
                override fun onResponse(call: Call<TmaResponse>, response: Response<TmaResponse>) {
                    if (response.isSuccessful) {
                        val responseResult = response.body()?.result?.objects?.output?.geometries
                        if (responseResult?.isNotEmpty() == true) {
                            val firstResult = responseResult[0]?.properties
                            val firstGaugeName = firstResult?.gaugeNameId
                            if (firstResult?.observations?.isNotEmpty() == true) {
                                val firstResultStatus = firstResult.observations[0]?.f4
                                val notificationText = context.getString(
                                    R.string.tma_status,
                                    firstGaugeName,
                                    firstResultStatus
                                )
                                waterLevelNotification.showNotification(notificationText)
                            }
                        }
                        resultStatus = Result.success()
                    } else {
                        resultStatus = Result.retry()
                    }
                }

                override fun onFailure(call: Call<TmaResponse>, t: Throwable) {
                    resultStatus = Result.failure()
                }
            })
            resultStatus
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
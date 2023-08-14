package com.steven.disaster

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.WorkerParameters
import com.steven.disaster.data.ApiService
import com.steven.disaster.data.response.TmaResponse
import com.steven.disaster.dummydata.ExpectedDataTma
import com.steven.disaster.dummydata.MainDispatcherRule
import com.steven.disaster.utils.WaterLevelNotification
import com.steven.disaster.utils.WaterLevelWorker
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class WaterLevelWorkerTest {
    private lateinit var worker: CoroutineWorker

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var waterLevelNotification: WaterLevelNotification

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var params: WorkerParameters

    @Before
    fun setup() {
        apiService = mockk(relaxed = true)
        waterLevelNotification = mockk(relaxed = true)
        context = mockk(relaxed = true)
        params = mockk(relaxed = true)
        worker = WaterLevelWorker(apiService, waterLevelNotification, context, params)
    }

    @Test
    fun `get tma data from api success`() = runTest {
        val mockCall: Call<TmaResponse> = mockk(relaxed = true)
        val mockResponse: Response<TmaResponse> = mockk(relaxed = true)

        every { apiService.getTma() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<TmaResponse> = args[0] as Callback<TmaResponse>
            callback.onResponse(mockCall, mockResponse)
        }
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns ExpectedDataTma.data

        val result = worker.doWork()
        assertTrue(result == Result.success())
    }

    @Test
    fun `get tma data from api success but empty`() = runTest {
        val mockCall: Call<TmaResponse> = mockk(relaxed = true)
        val mockResponse: Response<TmaResponse> = mockk(relaxed = true)

        every { apiService.getTma() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<TmaResponse> = args[0] as Callback<TmaResponse>
            callback.onResponse(mockCall, mockResponse)
        }
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns ExpectedDataTma.emptyData

        val result = worker.doWork()
        assertTrue(result == Result.success())
    }

    @Test
    fun `get tma data from api response not succesful`() = runTest {
        val mockCall: Call<TmaResponse> = mockk(relaxed = true)
        val mockResponse: Response<TmaResponse> = mockk(relaxed = true)

        every { apiService.getTma() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<TmaResponse> = args[0] as Callback<TmaResponse>
            callback.onResponse(mockCall, mockResponse)
        }
        every { mockResponse.isSuccessful } returns false

        val result = worker.doWork()
        assertTrue(result == Result.retry())
    }

    @Test
    fun `get tma data from api onFailure`() = runTest {
        val mockCall: Call<TmaResponse> = mockk(relaxed = true)

        every { apiService.getTma() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<TmaResponse> = args[0] as Callback<TmaResponse>
            callback.onFailure(mockCall, Throwable())
        }

        val result = worker.doWork()
        assertTrue(result == Result.failure())
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
package com.steven.disaster

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.steven.disaster.data.ApiService
import com.steven.disaster.data.response.DisasterResponse
import com.steven.disaster.dummydata.ExpectedDataDisaster
import com.steven.disaster.viewmodel.MainViewModel
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Suppress("UNCHECKED_CAST")
class MainViewModelTest {
    private lateinit var mainViewModel: MainViewModel

    @get: Rule
    val rule = InstantTaskExecutorRule()

    @MockK
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        apiService = mockk(relaxed = true)
        mainViewModel = MainViewModel(apiService)
    }

    @Test
    fun `checking whether mainViewModel returning expected result`() {
        val mockCall: Call<DisasterResponse> = mockk(relaxed = true)
        val mockResponse: Response<DisasterResponse> = mockk(relaxed = true)

        every { apiService.getReports() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<DisasterResponse> = args[0] as Callback<DisasterResponse>
            callback.onResponse(mockCall, mockResponse)
        }
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns ExpectedDataDisaster.data

        mainViewModel.getGeometriesItem()

        val actualData = mainViewModel.geometriesItem.value
        val expectedData = ExpectedDataDisaster.data.result?.objects?.output?.geometries

        assertTrue(actualData == expectedData)
        assertTrue(mainViewModel.isEmpty.value == false)
        assertTrue(mainViewModel.isFailure.value == false)
        assertTrue(mainViewModel.isLoading.value == false)
    }

    @Test
    fun `if geometries is empty`() {
        val mockCall: Call<DisasterResponse> = mockk(relaxed = true)
        val mockResponse: Response<DisasterResponse> = mockk(relaxed = true)

        every { apiService.getReports() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<DisasterResponse> = args[0] as Callback<DisasterResponse>
            callback.onResponse(mockCall, mockResponse)
        }
        every { mockResponse.isSuccessful } returns true
        every { mockResponse.body() } returns ExpectedDataDisaster.dataEmpty

        mainViewModel.getGeometriesItem()

        val actualData = mainViewModel.geometriesItem.value
        val expectedData = ExpectedDataDisaster.dataEmpty.result?.objects?.output?.geometries

        assertTrue(actualData == expectedData)
        assertTrue(mainViewModel.isEmpty.value == true)
        assertTrue(mainViewModel.isFailure.value == false)
        assertTrue(mainViewModel.isLoading.value == false)
    }

    @Test
    fun `on failure`() {
        val mockCall: Call<DisasterResponse> = mockk(relaxed = true)

        every { apiService.getReports() } returns mockCall
        every { mockCall.enqueue(any()) } answers {
            val callback: Callback<DisasterResponse> = args[0] as Callback<DisasterResponse>
            callback.onFailure(mockCall, Throwable())
        }

        mainViewModel.getGeometriesItem()
        assertTrue(mainViewModel.isEmpty.value == false)
        assertTrue(mainViewModel.isFailure.value == true)
        assertTrue(mainViewModel.isLoading.value == false)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}
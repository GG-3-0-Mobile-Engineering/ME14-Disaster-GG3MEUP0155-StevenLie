package com.steven.disaster.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.steven.disaster.R
import com.steven.disaster.data.ApiService
import com.steven.disaster.data.response.DisasterResponse
import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.data.response.TmaResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiService: ApiService,
    private val context: Application
) :
    ViewModel() {
    private val _geometriesItem = MutableLiveData<List<GeometriesItem?>?>()
    val geometriesItem: LiveData<List<GeometriesItem?>?> = _geometriesItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailure = MutableLiveData<Boolean>()
    val isFailure: LiveData<Boolean> = _isFailure

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    private val _tmaStatus = MutableLiveData<String?>()
    val tmaStatus: LiveData<String?> = _tmaStatus

    fun getGeometriesItem(locationId: String? = null, disasterType: String? = null) {
        _isLoading.value = true
        _isFailure.value = false
        _isEmpty.value = false
        val client = apiService.getReports(locationId, disasterType)
        client.enqueue(object : Callback<DisasterResponse> {
            override fun onResponse(
                call: Call<DisasterResponse>,
                response: Response<DisasterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseResult = response.body()?.result?.objects?.output?.geometries
                    _geometriesItem.value = responseResult
                    if (responseResult?.size == 0) {
                        _isEmpty.value = true
                    }
                }
            }

            override fun onFailure(call: Call<DisasterResponse>, t: Throwable) {
                _isLoading.value = false
                _isFailure.value = true
            }
        })
    }

    fun getFirstTmaStatus() {
        val client = apiService.getTma()
        client.enqueue(object : Callback<TmaResponse> {
            override fun onResponse(call: Call<TmaResponse>, response: Response<TmaResponse>) {
                if (response.isSuccessful) {
                    val responseResult = response.body()?.result?.objects?.output?.geometries
                    if (responseResult?.isNotEmpty() == true) {
                        val firstResult = responseResult[0]?.properties
                        val firstGaugeName = firstResult?.gaugeNameId
                        if (firstResult?.observations?.isNotEmpty() == true) {
                            val firstResultStatus = firstResult.observations[0]?.f4
                            _tmaStatus.value =
                                context.getString(
                                    R.string.tma_status,
                                    firstGaugeName,
                                    firstResultStatus
                                )
                        }
                    } else {
                        _tmaStatus.value = null
                    }
                }
            }

            override fun onFailure(call: Call<TmaResponse>, t: Throwable) {}
        })
    }
}
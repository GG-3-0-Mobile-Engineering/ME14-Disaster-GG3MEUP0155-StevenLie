package com.steven.disaster.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.steven.disaster.data.ApiConfig
import com.steven.disaster.data.response.DisasterResponse
import com.steven.disaster.data.response.GeometriesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _geometriesItem = MutableLiveData<List<GeometriesItem?>?>()
    val geometriesItem: LiveData<List<GeometriesItem?>?> = _geometriesItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isFailure = MutableLiveData<Boolean>()
    val isFailure: LiveData<Boolean> = _isFailure

    init {
        getGeometriesItem()
    }

    fun getGeometriesItem() {
        _isLoading.value = true
        _isFailure.value = false
        val client = ApiConfig.getApiService().getReports()
        client.enqueue(object : Callback<DisasterResponse> {
            override fun onResponse(
                call: Call<DisasterResponse>,
                response: Response<DisasterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _geometriesItem.value = response.body()?.result?.objects?.output?.geometries
                }
            }

            override fun onFailure(call: Call<DisasterResponse>, t: Throwable) {
                _isLoading.value = false
                _isFailure.value = true
            }
        })
    }

    fun getGeometriesItemByLocation(id: String?) {
        _isLoading.value = true
        _isFailure.value = false
        val client = ApiConfig.getApiService().getReports(id)
        client.enqueue(object : Callback<DisasterResponse> {
            override fun onResponse(
                call: Call<DisasterResponse>,
                response: Response<DisasterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _geometriesItem.value = response.body()?.result?.objects?.output?.geometries
                }
            }

            override fun onFailure(call: Call<DisasterResponse>, t: Throwable) {
                _isLoading.value = false
                _isFailure.value = true
            }
        })
    }

    fun getGeometriesItemByType(type: String) {
        _isLoading.value = true
        _isFailure.value = false
        val client = ApiConfig.getApiService().getReportByType(type)
        client.enqueue(object : Callback<DisasterResponse> {
            override fun onResponse(
                call: Call<DisasterResponse>,
                response: Response<DisasterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _geometriesItem.value = response.body()?.result?.objects?.output?.geometries
                }
            }

            override fun onFailure(call: Call<DisasterResponse>, t: Throwable) {
                _isLoading.value = false
                _isFailure.value = true
            }
        })
    }
}
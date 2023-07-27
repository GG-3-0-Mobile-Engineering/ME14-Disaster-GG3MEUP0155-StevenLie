package com.steven.disaster

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _geometriesItem = MutableLiveData<List<GeometriesItem?>?>()
    val geometriesItem: LiveData<List<GeometriesItem?>?> = _geometriesItem

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getGeometriesItem()
    }

    private fun getGeometriesItem() {
        _isLoading.value = true
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
            }
        })
    }
}
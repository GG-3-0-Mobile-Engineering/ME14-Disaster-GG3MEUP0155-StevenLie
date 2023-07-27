package com.steven.disaster

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("reports")
    fun getReports(@Query("admin") admin: String? = null) : Call<DisasterResponse>
}
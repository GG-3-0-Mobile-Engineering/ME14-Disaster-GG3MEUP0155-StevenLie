package com.steven.disaster.data

import com.steven.disaster.data.response.DisasterResponse
import com.steven.disaster.data.response.TmaResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("reports")
    fun getReports(@Query("admin") admin: String? = null): Call<DisasterResponse>

    @GET("reports")
    fun getReportByType(@Query("disaster") disaster: String): Call<DisasterResponse>

    @GET("floodgauges?admin=ID-JK")
    fun getTma(): Call<TmaResponse>
}
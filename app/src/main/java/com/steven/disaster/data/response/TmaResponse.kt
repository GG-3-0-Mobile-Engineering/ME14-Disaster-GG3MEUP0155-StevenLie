package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class TmaResponse(
    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)
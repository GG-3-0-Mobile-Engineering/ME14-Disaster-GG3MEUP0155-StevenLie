package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class Result(
    @field:SerializedName("objects")
    val objects: Objects? = null,
)
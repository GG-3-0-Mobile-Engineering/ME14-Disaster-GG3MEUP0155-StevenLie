package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class Output(
    @field:SerializedName("geometries")
    val geometries: List<GeometriesItem?>? = null,
)
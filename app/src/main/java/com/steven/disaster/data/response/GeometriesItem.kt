package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class GeometriesItem(
    @field:SerializedName("coordinates")
    val coordinates: List<Double?>? = null,

    @field:SerializedName("properties")
    val properties: Properties? = null
)
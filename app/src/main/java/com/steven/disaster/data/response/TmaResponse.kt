package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class TmaResponse(
    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)

data class OutputTma(
    @field:SerializedName("geometries")
    val geometries: List<GeometriesItem?>? = null,

    @field:SerializedName("type")
    val type: String? = null
)

data class PropertiesTma(
    @field:SerializedName("gaugenameid")
    val gaugeNameId: String? = null,

    @field:SerializedName("observations")
    val observations: List<ObservationsItem?>? = null,

    @field:SerializedName("gaugeid")
    val gaugeId: String? = null
)

data class ObservationsItem(
    @field:SerializedName("f1")
    val f1: String? = null,

    @field:SerializedName("f2")
    val f2: Int? = null,

    @field:SerializedName("f3")
    val f3: Int? = null,

    @field:SerializedName("f4")
    val f4: String? = null
)

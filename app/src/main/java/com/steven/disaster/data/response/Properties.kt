package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class Properties(
    @field:SerializedName("image_url")
    val imageUrl: String? = null,

    @field:SerializedName("disaster_type")
    val disasterType: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("tags")
    val tags: Tags? = null,

    @field:SerializedName("gaugenameid")
    val gaugeNameId: String? = null,

    @field:SerializedName("observations")
    val observations: List<ObservationsItem?>? = null,

    @field:SerializedName("gaugeid")
    val gaugeId: String? = null
)
package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class Tags(
    @field:SerializedName("instance_region_code")
    val instanceRegionCode: String? = null,
)
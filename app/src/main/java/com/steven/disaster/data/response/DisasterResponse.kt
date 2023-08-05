package com.steven.disaster.data.response

import com.google.gson.annotations.SerializedName

data class DisasterResponse(
    @field:SerializedName("result")
	val result: Result? = null,

    @field:SerializedName("statusCode")
	val statusCode: Int? = null
)

data class Objects(
	@field:SerializedName("output")
	val output: Output? = null
)

data class Tags(
	@field:SerializedName("instance_region_code")
	val instanceRegionCode: String? = null,
)

data class Output(
    @field:SerializedName("geometries")
	val geometries: List<GeometriesItem?>? = null,
)

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
	val tags: Tags? = null
)

data class GeometriesItem(
	@field:SerializedName("coordinates")
	val coordinates: List<Double?>? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null
)

data class Result(
    @field:SerializedName("objects")
	val objects: Objects? = null,
)

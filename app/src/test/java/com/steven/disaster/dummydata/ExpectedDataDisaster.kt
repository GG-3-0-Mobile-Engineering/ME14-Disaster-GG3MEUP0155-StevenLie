package com.steven.disaster.dummydata

import com.steven.disaster.data.response.DisasterResponse
import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.data.response.Objects
import com.steven.disaster.data.response.Output
import com.steven.disaster.data.response.Properties
import com.steven.disaster.data.response.Result
import com.steven.disaster.data.response.Tags

object ExpectedDataDisaster {
    val data = DisasterResponse(
        statusCode = 200,
        result = Result(
            objects = Objects(
                output = Output(
                    geometries = listOf(
                        GeometriesItem(
                            coordinates = listOf(1.0, -1.0),
                            properties = Properties(
                                imageUrl = "https://images.com",
                                disasterType = "flood",
                                createdAt = "2023",
                                source = "grasp",
                                status = "confirmed",
                                tags = Tags("ID-JK"),
                            )
                        )
                    )
                )
            )
        )
    )

    val dataEmpty = DisasterResponse(
        statusCode = 200,
        result = Result(
            objects = Objects(
                output = Output(
                    geometries = listOf()
                )
            )
        )
    )
}
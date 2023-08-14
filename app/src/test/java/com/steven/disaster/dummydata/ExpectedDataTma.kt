package com.steven.disaster.dummydata

import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.data.response.Objects
import com.steven.disaster.data.response.ObservationsItem
import com.steven.disaster.data.response.Output
import com.steven.disaster.data.response.Properties
import com.steven.disaster.data.response.Result
import com.steven.disaster.data.response.TmaResponse

object ExpectedDataTma {
    val data = TmaResponse(
        statusCode = 200,
        result = Result(
            objects = Objects(
                output = Output(
                    geometries = listOf(
                        GeometriesItem(
                            properties = Properties(
                                gaugeNameId = "bendungan",
                                gaugeId = "TMA",
                                observations = listOf(
                                    ObservationsItem(
                                        f4 = "siaga iv"
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )

    val emptyData = TmaResponse(
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
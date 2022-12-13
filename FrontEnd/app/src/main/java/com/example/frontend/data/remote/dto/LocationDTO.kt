package com.example.frontend.data.remote.dto

import com.example.frontend.domain.model.Location
import com.squareup.moshi.Json

data class LocationDTO(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "coordinateX") val coordinateX:Double,
    @field:Json(name = "coordinateY") val coordinateY: Double
)

fun LocationDTO.toLocation() : Location{
    return Location(
        id = id,
        name = name,
        lat = coordinateX,
        lng = coordinateY
    )
}
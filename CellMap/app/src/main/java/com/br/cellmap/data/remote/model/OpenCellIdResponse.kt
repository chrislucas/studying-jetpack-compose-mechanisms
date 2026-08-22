package com.br.cellmap.data.remote.model

import com.google.gson.annotations.SerializedName

data class OpenCellIdResponse(
    @SerializedName("lat") val lat: Double = 0.0,
    @SerializedName("lon") val lon: Double = 0.0,
    @SerializedName("range") val range: Int = 0,
    @SerializedName("status") val status: String? = null,
    @SerializedName("error") val error: String? = null
)

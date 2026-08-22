package com.br.cellmap.data.remote

import com.br.cellmap.data.remote.model.OpenCellIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCellIdApi {
    @GET("cell/get")
    suspend fun getCellLocation(
        @Query("key") key: String,
        @Query("mcc") mcc: String,
        @Query("mnc") mnc: String,
        @Query("lac") lac: Int,
        @Query("cellid") cellId: Long,
        @Query("format") format: String = "json"
    ): OpenCellIdResponse

    @GET("cell/getInArea")
    suspend fun getInArea(
        @Query("key") key: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json"
    ): List<OpenCellIdResponse>

    companion object {
        const val BASE_URL = "https://opencellid.org/"
    }
}

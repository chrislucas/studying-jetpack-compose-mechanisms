package com.br.cellmap.data.model

data class CellData(
    val mcc: String?,
    val mnc: String?,
    val lacTac: Int?,
    val cellId: Long?,
    val networkType: String,
    val signalDbm: Int?,
    val signalLevel: Int // 0-4
)

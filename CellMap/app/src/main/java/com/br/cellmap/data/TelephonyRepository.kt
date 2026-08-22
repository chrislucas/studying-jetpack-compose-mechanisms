package com.br.cellmap.data

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.*
import android.os.Build
import com.br.cellmap.data.model.CellData
import com.br.cellmap.data.remote.OpenCellIdApi
import com.br.cellmap.data.remote.model.OpenCellIdResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface TelephonyRepository {
    fun getCellData(): Flow<CellData?>
    suspend fun getTowerLocation(mcc: String, mnc: String, lac: Int, cellId: Long): OpenCellIdResponse?
    suspend fun getNearbyTowers(lat: Double, lon: Double): List<OpenCellIdResponse>
}

class TelephonyRepositoryImpl(private val context: Context) : TelephonyRepository {
    private val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    
    private val openCellIdApi = Retrofit.Builder()
        .baseUrl(OpenCellIdApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenCellIdApi::class.java)

    private val API_KEY = "YOUR_OPEN_CELL_ID_KEY"

    override suspend fun getTowerLocation(
        mcc: String,
        mnc: String,
        lac: Int,
        cellId: Long
    ): OpenCellIdResponse? {
        return try {
            openCellIdApi.getCellLocation(
                key = API_KEY,
                mcc = mcc,
                mnc = mnc,
                lac = lac,
                cellId = cellId
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getNearbyTowers(lat: Double, lon: Double): List<OpenCellIdResponse> {
        return try {
            openCellIdApi.getInArea(key = API_KEY, lat = lat, lon = lon)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @SuppressLint("MissingPermission")
    override fun getCellData(): Flow<CellData?> = callbackFlow {
        val callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            object : TelephonyCallback(), TelephonyCallback.CellInfoListener, TelephonyCallback.SignalStrengthsListener {
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) {
                    trySend(mapCellInfo(cellInfo, getSignalStrengthCompat()))
                }

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
                    trySend(mapCellInfo(telephonyManager.allCellInfo, signalStrength))
                }
            }
        } else {
            @Suppress("DEPRECATION")
            object : PhoneStateListener() {
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>?) {
                    trySend(mapCellInfo(cellInfo, null))
                }

                override fun onSignalStrengthsChanged(signalStrength: SignalStrength?) {
                    trySend(mapCellInfo(telephonyManager.allCellInfo, signalStrength))
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            telephonyManager.registerTelephonyCallback(context.mainExecutor, callback as TelephonyCallback)
        } else {
            @Suppress("DEPRECATION")
            telephonyManager.listen(callback as PhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO or PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
        }

        // Initial fetch
        trySend(mapCellInfo(telephonyManager.allCellInfo, getSignalStrengthCompat()))

        awaitClose {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyManager.unregisterTelephonyCallback(callback as TelephonyCallback)
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.listen(callback as PhoneStateListener, PhoneStateListener.LISTEN_NONE)
            }
        }
    }.onStart {
        // Optional: emit initial state if needed
    }

    private fun getSignalStrengthCompat(): SignalStrength? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            telephonyManager.signalStrength
        } else {
            null
        }
    }

    private fun mapCellInfo(cellInfoList: List<CellInfo>?, signalStrength: SignalStrength?): CellData? {
        val registeredCell = cellInfoList?.firstOrNull { it.isRegistered } ?: return null
        
        var mcc: String? = null
        var mnc: String? = null
        var lacTac: Int? = null
        var cellId: Long? = null
        var networkType = "Unknown"
        var signalDbm: Int? = null
        var signalLevel = 0

        @Suppress("DEPRECATION")
        when (registeredCell) {
            is CellInfoLte -> {
                val identity = registeredCell.cellIdentity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mcc = identity.mccString
                    mnc = identity.mncString
                } else {
                    mcc = identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                    mnc = identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                }
                lacTac = identity.tac
                cellId = identity.ci.toLong()
                networkType = "LTE"
                val strength = registeredCell.cellSignalStrength
                signalDbm = strength.dbm
                signalLevel = strength.level
            }
            is CellInfoGsm -> {
                val identity = registeredCell.cellIdentity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mcc = identity.mccString
                    mnc = identity.mncString
                } else {
                    mcc = identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                    mnc = identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                }
                lacTac = identity.lac
                cellId = identity.cid.toLong()
                networkType = "GSM"
                val strength = registeredCell.cellSignalStrength
                signalDbm = strength.dbm
                signalLevel = strength.level
            }
            is CellInfoWcdma -> {
                val identity = registeredCell.cellIdentity
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    mcc = identity.mccString
                    mnc = identity.mncString
                } else {
                    mcc = identity.mcc.takeIf { it != Int.MAX_VALUE }?.toString()
                    mnc = identity.mnc.takeIf { it != Int.MAX_VALUE }?.toString()
                }
                lacTac = identity.lac
                cellId = identity.cid.toLong()
                networkType = "WCDMA"
                val strength = registeredCell.cellSignalStrength
                signalDbm = strength.dbm
                signalLevel = strength.level
            }
            is CellInfoCdma -> {
                networkType = "CDMA"
                val strength = registeredCell.cellSignalStrength
                signalDbm = strength.dbm
                signalLevel = strength.level
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && registeredCell is CellInfoNr) {
            val identity = registeredCell.cellIdentity as CellIdentityNr
            mcc = identity.mccString
            mnc = identity.mncString
            lacTac = identity.tac
            cellId = identity.nci
            networkType = "5G NR"
            val strength = registeredCell.cellSignalStrength as CellSignalStrengthNr
            signalDbm = strength.dbm
            signalLevel = strength.level
        }

        // If signal info is missing from CellInfo, try getting it from SignalStrength
        if (signalDbm == null || signalDbm == Int.MAX_VALUE || signalDbm == -1) {
            signalStrength?.let {
                signalDbm = it.dbmCompat
                signalLevel = it.level
            }
        }

        return CellData(
            mcc = mcc,
            mnc = mnc,
            lacTac = lacTac,
            cellId = cellId,
            networkType = networkType,
            signalDbm = if (signalDbm != Int.MAX_VALUE && signalDbm != -1) signalDbm else null,
            signalLevel = signalLevel
        )
    }

    private val SignalStrength.dbmCompat: Int
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                cellSignalStrengths.firstOrNull()?.dbm ?: Int.MAX_VALUE
            } else {
                @Suppress("DEPRECATION")
                try {
                    val method = javaClass.getMethod("getDbm")
                    method.invoke(this) as Int
                } catch (e: Exception) {
                    Int.MAX_VALUE
                }
            }
        }
}

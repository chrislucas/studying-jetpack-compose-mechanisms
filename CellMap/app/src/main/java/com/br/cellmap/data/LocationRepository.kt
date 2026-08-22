package com.br.cellmap.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface LocationRepository {
    fun getUserLocation(): Flow<Location?>
}

class LocationRepositoryImpl(private val context: Context) : LocationRepository {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getUserLocation(): Flow<Location?> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                trySend(result.lastLocation)
            }
        }

        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        
        // Initial location
        client.lastLocation.addOnSuccessListener { location ->
            trySend(location)
        }

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}

package com.br.samplegeocodingapi.core.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.CoroutineContext

/*
    https://share.google/aimode/QJZcJOixr0lhjxSlR
 */


object LocationHelper {


    private const val MAX_RESULTS = 1

    /**
     * Busca o código postal (CEP) de forma assíncrona/síncrona idiomática.
     *
     * @param onCancellation Referência da função que gerencia o descarte do valor se a corrotina for cancelada.
     */
    suspend fun getZipCode(
        context: Context,
        latitude: Double,
        longitude: Double,
        onCancellation: (
            cause: Throwable,
            value: String?,
            context: CoroutineContext
        ) -> Unit = { _, _, _ -> }
    ): String? {
        val appContext = context.applicationContext

        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(appContext, Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    val listener = object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            val zipCode = addresses.firstOrNull()?.postalCode

                            if (continuation.isActive) {
                                // Passa a função recebida por parâmetro diretamente como referência para o resume
                                continuation.resume(zipCode, onCancellation)
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) {
                                // Em caso de erro, repassa a função padrão ou customizada para o valor nulo
                                continuation.resume(null, onCancellation)
                            }
                        }
                    }

                    geocoder.getFromLocation(latitude, longitude, MAX_RESULTS, listener)
                }
            } else {
                try {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(latitude, longitude, MAX_RESULTS)
                    addresses?.firstOrNull()?.postalCode
                } catch (_: Exception) {
                    null
                }
            }
        }
    }
}
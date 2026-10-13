package com.br.feature.wallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.br.navigation.AppDestination
import com.br.navigation.DeepLinkParser

/**
 * Entrypoint do módulo Carteira.
 *
 * É aberta por deep link `app://library/wallet` vindo do :app (ou de qualquer
 * componente). Demonstra o lado **receptor** do deep link entre Activities de
 * módulos diferentes: lemos `intent.data`, convertemos em [AppDestination] com
 * o [DeepLinkParser] e então montamos a navegação interna via [WalletNavHost].
 */
class WalletActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Lado receptor do deep link. Para a Carteira não há argumentos, mas
        // validamos o destino para deixar o padrão explícito e didático.
        val destination = DeepLinkParser.parse(intent?.data?.toString())
        require(destination == null || destination is AppDestination.Wallet) {
            "WalletActivity recebeu um deep link inesperado: $destination"
        }

        setContent {
            MaterialTheme {
                val navigator = rememberWalletNavigator()
                WalletNavHost(
                    navigator = navigator,
                    onExit = { finish() },
                )
            }
        }
    }
}

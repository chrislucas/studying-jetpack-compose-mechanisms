package com.br.feature.transfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.br.navigation.AppDestination
import com.br.navigation.DeepLinkParser

/**
 * Entrypoint do módulo Transferência.
 *
 * Lado **receptor** de um deep link que carrega argumentos:
 * `app://library/transfer?amount=50.0&toAccount=ACC-2`.
 *
 * Lemos `intent.data`, convertemos em [AppDestination.Transfer] com o
 * [DeepLinkParser] e usamos os valores para pré-preencher o formulário —
 * exemplo prático de passar informação de uma Activity para outra via deep link.
 */
class TransferActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val destination = DeepLinkParser.parse(intent?.data?.toString())
        val transfer = destination as? AppDestination.Transfer

        val startRoute = TransferRoute.Form(
            prefilledAmount = transfer?.amount,
            prefilledToAccount = transfer?.toAccount,
        )

        setContent {
            MaterialTheme {
                val navigator = rememberTransferNavigator(startRoute)
                TransferNavHost(
                    navigator = navigator,
                    onExit = { finish() },
                )
            }
        }
    }
}

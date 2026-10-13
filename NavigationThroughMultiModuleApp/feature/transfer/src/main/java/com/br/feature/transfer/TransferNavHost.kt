package com.br.feature.transfer

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.br.navigation.Navigator

/**
 * Host de navegação interna do módulo Transferência.
 *
 * Demonstra os dois tipos de passagem de dados pedidos:
 * 1. **Entre Activities (deep link):** a rota inicial [TransferRoute.Form] é
 *    construída a partir dos argumentos recebidos no deep link (startRoute).
 * 2. **Entre composables (Navigation 3):** os valores do formulário viajam para
 *    a confirmação e o recibo como argumentos das próprias rotas.
 *
 * @param startRoute rota inicial, normalmente derivada do deep link na Activity.
 */
@Composable
fun TransferNavHost(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    onExit: () -> Unit = {},
) {
    Scaffold(modifier = modifier) { innerPadding ->
        NavDisplay(
            backStack = navigator.backStack,
            modifier = Modifier.padding(innerPadding),
            onBack = {
                if (!navigator.goBack()) onExit()
            },
            entryProvider = { key ->
                when (key) {
                    is TransferRoute.Form -> NavEntry(key) {
                        TransferFormScreen(
                            prefilledAmount = key.prefilledAmount,
                            prefilledToAccount = key.prefilledToAccount,
                            onContinue = { amount, toAccount ->
                                navigator.goTo(
                                    TransferRoute.Confirmation(amount, toAccount),
                                )
                            },
                        )
                    }

                    is TransferRoute.Confirmation -> NavEntry(key) {
                        TransferConfirmationScreen(
                            amount = key.amount,
                            toAccount = key.toAccount,
                            onConfirm = {
                                // replaceTop: após confirmar não faz sentido voltar
                                // para a confirmação; o back do recibo sai do fluxo.
                                navigator.replaceTop(
                                    TransferRoute.Receipt(key.amount, key.toAccount),
                                )
                            },
                            onBack = { navigator.goBack() },
                        )
                    }

                    is TransferRoute.Receipt -> NavEntry(key) {
                        TransferReceiptScreen(
                            amount = key.amount,
                            toAccount = key.toAccount,
                            onDone = onExit,
                        )
                    }

                    else -> error("Rota desconhecida: $key")
                }
            },
        )
    }
}

/** Cria um [Navigator] iniciado no formulário, com eventual pré-preenchimento. */
@Composable
fun rememberTransferNavigator(startRoute: TransferRoute.Form): Navigator =
    remember(startRoute) { Navigator(startDestination = startRoute) }

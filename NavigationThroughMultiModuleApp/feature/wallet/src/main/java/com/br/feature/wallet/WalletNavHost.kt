package com.br.feature.wallet

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.br.navigation.Navigator

/**
 * Host de navegação **interna** do módulo Carteira.
 *
 * Monta o [NavDisplay] do Navigation 3 a partir do back stack de um
 * [Navigator] (injeção manual — recebido por parâmetro, não via framework de DI).
 *
 * O `entryProvider` resolve cada [WalletRoute] para seu composable. A navegação
 * entre composables é feita apenas empilhando/desempilhando rotas no back stack.
 * Note como [WalletRoute.AccountDetail] carrega o `accountId`: é assim que se
 * passa informação entre composables com Navigation 3.
 *
 * Extraído da Activity para poder ser testado por UI (Robolectric + Compose)
 * sem depender do ciclo de vida de Activity.
 *
 * @param onExit chamado quando o usuário tenta voltar além da raiz (fim do módulo).
 */
@Composable
fun WalletNavHost(
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
                    is WalletRoute.Menu -> NavEntry(key) {
                        WalletMenuScreen(
                            onOpenAccounts = { navigator.goTo(WalletRoute.Accounts) },
                        )
                    }

                    is WalletRoute.Accounts -> NavEntry(key) {
                        AccountsScreen(
                            accounts = WalletData.accounts,
                            onAccountClick = { accountId ->
                                navigator.goTo(WalletRoute.AccountDetail(accountId))
                            },
                        )
                    }

                    is WalletRoute.AccountDetail -> NavEntry(key) {
                        AccountDetailScreen(
                            account = WalletData.byId(key.accountId),
                            onBack = { navigator.goBack() },
                        )
                    }

                    else -> error("Rota desconhecida: $key")
                }
            },
        )
    }
}

/** Cria um [Navigator] já posicionado no menu do módulo. */
@Composable
fun rememberWalletNavigator(): Navigator =
    remember { Navigator(startDestination = WalletRoute.Menu) }

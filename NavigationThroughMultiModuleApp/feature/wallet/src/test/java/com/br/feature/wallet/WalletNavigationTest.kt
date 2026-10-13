package com.br.feature.wallet

import com.br.navigation.Navigator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Teste de unidade (JVM puro) do módulo Carteira.
 *
 * Verifica a lógica de navegação interna sem subir UI: ao abrir uma conta,
 * o id correto é empilhado como argumento da rota de detalhe — é assim que
 * passamos informação entre composables no Navigation 3.
 */
class WalletNavigationTest {

    @Test
    fun `navigate from menu to accounts then to detail carries accountId`() {
        val navigator = Navigator(startDestination = WalletRoute.Menu)

        navigator.goTo(WalletRoute.Accounts)
        navigator.goTo(WalletRoute.AccountDetail(accountId = "ACC-2"))

        val top = navigator.current
        assertTrue(top is WalletRoute.AccountDetail)
        assertEquals("ACC-2", (top as WalletRoute.AccountDetail).accountId)
    }

    @Test
    fun `wallet data resolves account by id`() {
        val account = WalletData.byId("ACC-2")

        assertEquals("Poupança", account?.name)
    }
}

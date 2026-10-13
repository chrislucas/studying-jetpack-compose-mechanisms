package com.br.feature.transfer

import com.br.navigation.Navigator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Teste de unidade (JVM puro) do módulo Transferência.
 *
 * Verifica que o fluxo Form -> Confirmation -> Receipt carrega os argumentos
 * corretos entre as rotas (passagem de dados entre composables) e que
 * `replaceTop` evita voltar para a confirmação após concluir.
 */
class TransferFlowTest {

    @Test
    fun `form to confirmation carries amount and account`() {
        val navigator = Navigator(startDestination = TransferRoute.Form())

        navigator.goTo(TransferRoute.Confirmation(amount = 50.0, toAccount = "ACC-2"))

        val top = navigator.current
        assertTrue(top is TransferRoute.Confirmation)
        top as TransferRoute.Confirmation
        assertEquals(50.0, top.amount, 0.0)
        assertEquals("ACC-2", top.toAccount)
    }

    @Test
    fun `confirm replaces confirmation with receipt on top`() {
        val navigator = Navigator(startDestination = TransferRoute.Form())
        navigator.goTo(TransferRoute.Confirmation(amount = 50.0, toAccount = "ACC-2"))

        navigator.replaceTop(TransferRoute.Receipt(amount = 50.0, toAccount = "ACC-2"))

        assertTrue(navigator.current is TransferRoute.Receipt)
        // Back stack: Form, Receipt (a confirmação foi substituída).
        assertEquals(2, navigator.backStack.size)
        assertTrue(navigator.backStack.first() is TransferRoute.Form)
    }

    @Test
    fun `deep link prefill populates the start form`() {
        val startRoute = TransferRoute.Form(prefilledAmount = 99.9, prefilledToAccount = "ACC-9")

        assertEquals(99.9, startRoute.prefilledAmount)
        assertEquals("ACC-9", startRoute.prefilledToAccount)
    }
}

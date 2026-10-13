package com.br.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Testes de unidade do [DeepLinkFactory], incluindo o "round-trip":
 * construir a URL de um destino e reparsear deve devolver o mesmo destino.
 */
class DeepLinkFactoryTest {

    @Test
    fun `urlFor wallet builds expected url`() {
        assertEquals("app://library/wallet", DeepLinkFactory.urlFor(AppDestination.Wallet))
    }

    @Test
    fun `urlFor transfer with args builds query string`() {
        val url = DeepLinkFactory.urlFor(
            AppDestination.Transfer(amount = 50.0, toAccount = "ACC-9"),
        )

        assertEquals("app://library/transfer?amount=50.0&toAccount=ACC-9", url)
    }

    @Test
    fun `urlFor transfer without args omits query`() {
        assertEquals(
            "app://library/transfer",
            DeepLinkFactory.urlFor(AppDestination.Transfer()),
        )
    }

    @Test
    fun `factory and parser round-trip for wallet`() {
        val destination = AppDestination.Wallet
        val roundTripped = DeepLinkParser.parse(DeepLinkFactory.urlFor(destination))

        assertEquals(destination, roundTripped)
    }

    @Test
    fun `factory and parser round-trip for transfer with args`() {
        val destination = AppDestination.Transfer(amount = 10.5, toAccount = "ACC-1")
        val roundTripped = DeepLinkParser.parse(DeepLinkFactory.urlFor(destination))

        assertEquals(destination, roundTripped)
    }
}

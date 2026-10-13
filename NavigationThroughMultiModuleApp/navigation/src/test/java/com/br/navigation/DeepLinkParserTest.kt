package com.br.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Testes de unidade (JVM pura, JUnit) do [DeepLinkParser].
 *
 * Validam o contrato de deep link usado para navegar entre Activities de
 * módulos diferentes. Como o parser não depende de `android.net.Uri`, estes
 * testes rodam direto na JVM, sem Robolectric.
 */
class DeepLinkParserTest {

    @Test
    fun `parse wallet deep link returns Wallet destination`() {
        val result = DeepLinkParser.parse("app://library/wallet")

        assertEquals(AppDestination.Wallet, result)
    }

    @Test
    fun `parse transfer deep link extracts amount and toAccount`() {
        val result = DeepLinkParser.parse("app://library/transfer?amount=123.45&toAccount=ACC-2")

        assertEquals(AppDestination.Transfer(amount = 123.45, toAccount = "ACC-2"), result)
    }

    @Test
    fun `parse transfer without query yields null arguments`() {
        val result = DeepLinkParser.parse("app://library/transfer")

        assertEquals(AppDestination.Transfer(amount = null, toAccount = null), result)
    }

    @Test
    fun `parse decodes encoded spaces in query values`() {
        val result = DeepLinkParser.parse("app://library/transfer?toAccount=John%20Doe")

        assertEquals(AppDestination.Transfer(toAccount = "John Doe"), result)
    }

    @Test
    fun `parse ignores malformed amount`() {
        val result = DeepLinkParser.parse("app://library/transfer?amount=not-a-number")

        assertEquals(AppDestination.Transfer(amount = null), result)
    }

    @Test
    fun `parse returns null for unknown path`() {
        assertNull(DeepLinkParser.parse("app://library/unknown"))
    }

    @Test
    fun `parse returns null for wrong scheme`() {
        assertNull(DeepLinkParser.parse("https://library/wallet"))
    }

    @Test
    fun `parse returns null for wrong host`() {
        assertNull(DeepLinkParser.parse("app://other/wallet"))
    }

    @Test
    fun `parse returns null for null or blank input`() {
        assertNull(DeepLinkParser.parse(null))
        assertNull(DeepLinkParser.parse(""))
        assertNull(DeepLinkParser.parse("   "))
    }
}

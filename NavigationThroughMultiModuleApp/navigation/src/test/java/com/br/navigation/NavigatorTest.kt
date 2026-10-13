package com.br.navigation

import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.navigation3.runtime.NavKey

/**
 * Testes de unidade do [Navigator].
 *
 * Demonstram o uso do MockK para fabricar rotas [NavKey] sem precisar de
 * implementações reais — útil quando a rota traria dependências pesadas.
 * O back stack usa `mutableStateListOf` do Compose runtime, que funciona na
 * JVM pura, então nenhum Robolectric é necessário aqui.
 */
class NavigatorTest {

    private val start = mockk<NavKey>()
    private val screenA = mockk<NavKey>()
    private val screenB = mockk<NavKey>()

    @Test
    fun `starts with the start destination on top`() {
        val navigator = Navigator(startDestination = start)

        assertEquals(start, navigator.current)
        assertEquals(1, navigator.backStack.size)
        assertFalse(navigator.canGoBack())
    }

    @Test
    fun `goTo pushes a new destination`() {
        val navigator = Navigator(startDestination = start)

        navigator.goTo(screenA)

        assertEquals(screenA, navigator.current)
        assertEquals(listOf(start, screenA), navigator.backStack.toList())
        assertTrue(navigator.canGoBack())
    }

    @Test
    fun `goBack pops the top destination`() {
        val navigator = Navigator(startDestination = start)
        navigator.goTo(screenA)

        val popped = navigator.goBack()

        assertTrue(popped)
        assertEquals(start, navigator.current)
    }

    @Test
    fun `goBack at root returns false and keeps the root`() {
        val navigator = Navigator(startDestination = start)

        val popped = navigator.goBack()

        assertFalse(popped)
        assertEquals(start, navigator.current)
        assertEquals(1, navigator.backStack.size)
    }

    @Test
    fun `replaceTop swaps the current destination without growing the stack`() {
        val navigator = Navigator(startDestination = start)
        navigator.goTo(screenA)

        navigator.replaceTop(screenB)

        assertEquals(screenB, navigator.current)
        assertEquals(listOf(start, screenB), navigator.backStack.toList())
    }
}

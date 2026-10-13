package com.br.navigationthroughmultimoduleapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Teste de UI da [HomeScreen] (Robolectric + Compose Test na JVM).
 *
 * Verifica que o menu é acessível (localizável por `contentDescription`) e que
 * cada botão aciona a navegação correspondente — aqui representada por lambdas,
 * já que a navegação real dispara Intents de deep link no device.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class HomeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menuButtonsAreAccessibleAndInvokeCallbacks() {
        var openedWallet = false
        var openedTransfer = false

        composeTestRule.setContent {
            HomeScreen(
                onOpenWallet = { openedWallet = true },
                onOpenTransfer = { openedTransfer = true },
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Abrir Carteira via deep link")
            .assertIsDisplayed()
            .performClick()

        composeTestRule
            .onNodeWithContentDescription("Abrir Transferência via deep link")
            .assertIsDisplayed()
            .performClick()

        assertTrue(openedWallet)
        assertTrue(openedTransfer)
    }
}

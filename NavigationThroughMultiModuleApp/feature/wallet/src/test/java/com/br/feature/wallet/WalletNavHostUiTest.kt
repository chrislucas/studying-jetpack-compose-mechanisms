package com.br.feature.wallet

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Teste de UI do módulo Carteira rodando na JVM via Robolectric + Compose Test.
 *
 * Exercita a navegação entre composables (menu -> contas -> detalhe) e usa a
 * semântica de acessibilidade (`contentDescription`) para localizar e clicar
 * nos elementos — exatamente como um leitor de tela os encontraria.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WalletNavHostUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigatesFromMenuToAccountDetailThroughComposables() {
        composeTestRule.setContent {
            WalletNavHost(navigator = rememberWalletNavigator())
        }

        // Menu inicial.
        composeTestRule.onNodeWithText("Carteira").assertIsDisplayed()

        // Abre a lista de contas via descrição de acessibilidade.
        composeTestRule.onNodeWithContentDescription("Abrir lista de contas").performClick()
        composeTestRule.onNodeWithText("Minhas contas").assertIsDisplayed()

        // Clica numa conta (achada pela semântica agregada do card).
        composeTestRule
            .onNodeWithContentDescription("Conta Poupança", substring = true)
            .performClick()

        // Detalhe exibe a conta correta — argumento passado entre composables.
        composeTestRule.onNodeWithText("Poupança").assertIsDisplayed()
        composeTestRule.onNodeWithText("Identificador: ACC-2").assertIsDisplayed()
    }
}

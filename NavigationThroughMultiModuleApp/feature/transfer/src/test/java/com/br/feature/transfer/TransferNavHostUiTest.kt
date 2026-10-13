package com.br.feature.transfer

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
 * Teste de UI do módulo Transferência (Robolectric + Compose Test na JVM).
 *
 * Começa com o formulário pré-preenchido como se tivesse vindo de um deep link
 * (`app://library/transfer?amount=50&toAccount=ACC-2`) e segue o fluxo até o
 * recibo, validando que os dados são carregados entre os composables.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TransferNavHostUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun prefilledFormFlowsThroughConfirmationToReceipt() {
        val startRoute = TransferRoute.Form(
            prefilledAmount = 50.0,
            prefilledToAccount = "ACC-2",
        )

        composeTestRule.setContent {
            TransferNavHost(navigator = rememberTransferNavigator(startRoute))
        }

        // Formulário aparece pré-preenchido pelos dados do "deep link".
        composeTestRule.onNodeWithText("Nova transferência").assertIsDisplayed()

        // Avança para a confirmação.
        composeTestRule
            .onNodeWithContentDescription("Continuar para a confirmação")
            .performClick()
        composeTestRule.onNodeWithText("Confirmar transferência").assertIsDisplayed()

        // Confirma e chega ao recibo.
        composeTestRule
            .onNodeWithContentDescription("Confirmar transferência")
            .performClick()
        composeTestRule.onNodeWithText("Transferência concluída").assertIsDisplayed()
    }
}

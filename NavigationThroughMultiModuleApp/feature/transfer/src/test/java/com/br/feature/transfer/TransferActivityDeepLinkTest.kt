package com.br.feature.transfer

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * Teste de UI do [TransferActivity] como **receptor de deep link**.
 *
 * Diferente do [TransferNavHostUiTest] (que injeta a rota inicial direto no
 * host), aqui iniciamos a **Activity real** com um `Intent(ACTION_VIEW)`
 * carregando a URI de deep link, exatamente como o sistema faria ao resolver
 * `app://library/transfer?amount=50.0&toAccount=ACC-2`. Validamos que:
 *
 * 1. o [DeepLinkParser] é acionado no `onCreate` e os argumentos chegam ao
 *    `TransferRoute.Form` inicial (pré-preenchimento do formulário);
 * 2. a UI resultante reflete esses valores e o fluxo prossegue normalmente.
 *
 * Usamos [createEmptyComposeRule] + `Robolectric.buildActivity` porque o
 * conteúdo Compose é definido pela própria Activity (via `setContent`), e
 * precisamos controlar o `Intent` de entrada.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TransferActivityDeepLinkTest {

    // Mantemos a API estável (v1). A v2 usa StandardTestDispatcher e exigiria
    // sincronização manual, o que fugiria do foco didático deste exemplo.
    @Suppress("DEPRECATION")
    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun setLocale() {
        // formatBrl() usa o Locale default; fixamos pt-BR para que o separador
        // decimal seja vírgula (R$ 50,00) de forma determinística no CI/local.
        Locale.setDefault(Locale.forLanguageTag("pt-BR"))
    }

    private fun launchWith(uri: String?): TransferActivity {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(context, TransferActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            if (uri != null) data = Uri.parse(uri)
        }
        return Robolectric.buildActivity(TransferActivity::class.java, intent)
            .setup()
            .get()
    }

    @Test
    fun deepLinkWithArguments_prefillsForm() {
        launchWith("app://library/transfer?amount=50.0&toAccount=ACC-2")

        // O formulário é a tela inicial e deve estar pré-preenchido.
        composeTestRule.onNodeWithText("Nova transferência").assertIsDisplayed()
        composeTestRule
            .onNodeWithContentDescription("Campo de valor da transferência")
            .assertTextContains("50.0")
        composeTestRule
            .onNodeWithContentDescription("Campo de conta de destino")
            .assertTextContains("ACC-2")
    }

    @Test
    fun deepLinkWithArguments_flowsToReceiptCarryingData() {
        launchWith("app://library/transfer?amount=50.0&toAccount=ACC-2")

        // Como já vem válido pelo deep link, conseguimos avançar direto.
        composeTestRule
            .onNodeWithContentDescription("Continuar para a confirmação")
            .performClick()

        // A confirmação exibe os valores recebidos via deep link.
        composeTestRule
            .onNodeWithContentDescription("Transferir R$ 50,00 para a conta ACC-2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("Confirmar transferência")
            .performClick()
        composeTestRule.onNodeWithText("Transferência concluída").assertIsDisplayed()
    }

    @Test
    fun deepLinkWithoutArguments_startsWithEmptyForm() {
        launchWith("app://library/transfer")

        composeTestRule.onNodeWithText("Nova transferência").assertIsDisplayed()
        // Sem argumentos, o botão continuar fica desabilitado (formulário vazio).
        composeTestRule
            .onNodeWithContentDescription("Continuar para a confirmação")
            .assertIsDisplayed()
    }
}

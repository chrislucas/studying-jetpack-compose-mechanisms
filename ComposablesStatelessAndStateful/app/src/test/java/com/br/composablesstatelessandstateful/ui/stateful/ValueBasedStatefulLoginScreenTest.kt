package com.br.composablesstatelessandstateful.ui.stateful

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Testes unitários (host/JVM) para [ValueBasedStatefulLoginScreen].
 *
 * Executam com Robolectric usando a Compose UI Test API (createComposeRule).
 * Isso permite rodar os mesmos tipos de assert de um teste instrumentado,
 * porém na JVM local, sem necessidade de device/emulador.
 *
 * Os elementos são localizados por testTag (ver [ValueBasedLoginTestTags]),
 * que é mais robusto a mudanças de texto/label do que onNodeWithText.
 *
 * Observações de setup (ver app/build.gradle.kts):
 *  - testOptions.unitTests.isIncludeAndroidResources = true  -> usa o manifest
 *    mesclado com o ui-test-manifest (que declara a ComponentActivity).
 *  - testImplementation do robolectric + ui-test-junit4 + ui-test-manifest.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ValueBasedStatefulLoginScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun exibeCampos() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.EMAIL_FIELD).assertExists()
        composeRule.onNodeWithTag(ValueBasedLoginTestTags.PASSWORD_FIELD).assertExists()
        composeRule.onNodeWithTag(ValueBasedLoginTestTags.SUBMIT_BUTTON).assertExists()
    }

    @Test
    fun botaoComecaDesabilitado() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.SUBMIT_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun botaoHabilitaComEmailESenhaValidos() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.EMAIL_FIELD)
            .performTextInput("user@email.com")
        composeRule.onNodeWithTag(ValueBasedLoginTestTags.PASSWORD_FIELD)
            .performTextInput("123456")

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.SUBMIT_BUTTON).assertIsEnabled()
    }

    @Test
    fun botaoPermaneceDesabilitadoComEmailInvalido() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.EMAIL_FIELD)
            .performTextInput("invalido")
        composeRule.onNodeWithTag(ValueBasedLoginTestTags.PASSWORD_FIELD)
            .performTextInput("123456")

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.SUBMIT_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun botaoPermaneceDesabilitadoComSenhaCurta() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.EMAIL_FIELD)
            .performTextInput("user@email.com")
        composeRule.onNodeWithTag(ValueBasedLoginTestTags.PASSWORD_FIELD)
            .performTextInput("123")

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.SUBMIT_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun alternaVisibilidadeDaSenha() {
        composeRule.setContent { ValueBasedStatefulLoginScreen() }

        // Começa oculta -> o botão (localizado por tag) expõe "Mostrar senha".
        composeRule.onNodeWithContentDescription("Mostrar senha").assertExists()

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.TOGGLE_PASSWORD_VISIBILITY)
            .performClick()

        // Após clicar, passa a oferecer "Ocultar senha".
        composeRule.onNodeWithContentDescription("Ocultar senha").assertExists()
    }
}

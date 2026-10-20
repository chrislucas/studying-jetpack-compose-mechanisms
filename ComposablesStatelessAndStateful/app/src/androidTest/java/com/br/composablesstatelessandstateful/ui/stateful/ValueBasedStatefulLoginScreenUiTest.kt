package com.br.composablesstatelessandstateful.ui.stateful

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Teste de UI instrumentado para [ValueBasedStatefulLoginScreen].
 *
 * Executa em device/emulador com AndroidJUnit4, usando a mesma Compose UI
 * Test API (createComposeRule) dos testes unitários Robolectric. Os elementos
 * são localizados por testTag (ver [ValueBasedLoginTestTags]).
 */
@RunWith(AndroidJUnit4::class)
class ValueBasedStatefulLoginScreenUiTest {

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

        composeRule.onNodeWithContentDescription("Mostrar senha").assertExists()

        composeRule.onNodeWithTag(ValueBasedLoginTestTags.TOGGLE_PASSWORD_VISIBILITY)
            .performClick()

        composeRule.onNodeWithContentDescription("Ocultar senha").assertExists()
    }
}

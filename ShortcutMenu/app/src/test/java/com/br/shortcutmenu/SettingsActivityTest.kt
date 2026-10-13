package com.br.shortcutmenu

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Testes de navegação da [SettingsActivity] rodando como unit test com Robolectric.
 *
 * Usamos [createAndroidComposeRule] (pacote v2) que lança a Activity REAL — ela
 * executa onCreate/setContent e configura o NavHost. Diferente de
 * createComposeRule(), aqui NÃO chamamos setContent{}: o conteúdo vem da Activity.
 *
 * Observação sobre a intent: o createAndroidComposeRule lança a Activity com a
 * intent PADRÃO (sem ACTION_VIEW). Por isso o LaunchedEffect(intent) de navegação
 * por atalho não é acionado e o app inicia no startDestination "home". Estes
 * testes cobrem o estado inicial e a navegação por CLIQUE (Home -> Settings).
 *
 * Pré-requisitos no build (já configurados):
 *   - testOptions.unitTests.isIncludeAndroidResources = true
 *   - testImplementation(androidx.compose.ui:ui-test-manifest)
 *   - Activity declarada no AndroidManifest.xml
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SettingsActivity>()

    @Test
    fun `given default config when activity opens then home screen is displayed`() {
        // startDestination = "home". A Home exibe o TopAppBar "Home".
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
        // Config padrão é desabilitada (false) -> texto "Inativo".
        composeTestRule.onNodeWithText("Inativo").assertIsDisplayed()
    }

    @Test
    fun `given home screen when clicking configuracoes then navigates to settings`() {
        // Clica no botão que navega para o destino "settings".
        composeTestRule.onNodeWithText("Configurações").performClick()

        // A tela de Settings exibe o TopAppBar "Settings" e o label do switch.
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ativar modo especial").assertIsDisplayed()
    }
}

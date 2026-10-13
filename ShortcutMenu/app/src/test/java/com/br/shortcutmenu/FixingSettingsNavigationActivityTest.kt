package com.br.shortcutmenu

import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Cenários de lançamento PADRÃO da [FixingSettingsNavigationActivity] (sem
 * ACTION_VIEW) rodando como unit test com Robolectric.
 *
 * O [createAndroidComposeRule] (v2) lança a Activity REAL com a intent padrão,
 * então o app inicia no startDestination "home". Cobrimos o estado inicial e a
 * navegação por CLIQUE (Home -> Settings).
 *
 * Pré-requisitos no build (já configurados):
 *   - testOptions.unitTests.isIncludeAndroidResources = true
 *   - testImplementation(androidx.compose.ui:ui-test-manifest)
 *   - Activity declarada no AndroidManifest.xml
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FixingSettingsNavigationActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<FixingSettingsNavigationActivity>()

    @Test
    fun `given default launch when activity opens then home screen is displayed`() {
        composeTestRule.onNodeWithText("Home").assertIsDisplayed()
    }

    @Test
    fun `given home screen when clicking configuracoes then navigates to settings`() {
        composeTestRule.onNodeWithText("Configurações").performClick()

        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ativar modo especial").assertIsDisplayed()
    }
}

/**
 * Cenário de lançamento pela INTENT de atalho (ACTION_VIEW) da
 * [FixingSettingsNavigationActivity].
 *
 * Construímos o [AndroidComposeTestRule] a partir de um [ActivityScenarioRule]
 * que recebe uma intent customizada com action = ACTION_VIEW. Assim a Activity
 * é lançada já com a intent do atalho e o LaunchedEffect(intent) deve redirecionar
 * para a tela de Settings assim que o NavHost estiver composto.
 *
 * Observação: o fluxo de onNewIntent (app já aberto em segundo plano) é melhor
 * validado no teste instrumentado (androidTest), onde conseguimos reentregar a
 * intent via singleTop.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FixingSettingsNavigationActivityViewIntentTest {

    private val viewIntent: Intent =
        Intent(
            ApplicationProvider.getApplicationContext(),
            FixingSettingsNavigationActivity::class.java
        ).apply { action = Intent.ACTION_VIEW }

    @get:Rule
    val composeTestRule =
        // AndroidComposeTestRule conecta as asserções de Compose a uma Activity
        // controlada por um launcher de ciclo de vida. Aqui usamos o construtor
        // de 2 argumentos:
        //
        // - activityRule: a regra JUnit que efetivamente LANÇA e encerra a
        //   Activity. Usamos ActivityScenarioRule com a `viewIntent` (ACTION_VIEW),
        //   para que a Activity suba já com a intent do atalho. É a partir dessa
        //   regra que a Activity em execução é obtida.
        //
        // - activityProvider: função que, dada a activityRule, DEVOLVE a instância
        //   da Activity em execução. O AndroidComposeTestRule precisa dessa
        //   referência para localizar a janela/host do Compose e resolver a árvore
        //   de semântica (onNodeWithText, etc.). Sem uma Activity, não há árvore
        //   para inspecionar.
        AndroidComposeTestRule(
            activityRule = ActivityScenarioRule<FixingSettingsNavigationActivity>(viewIntent),
            activityProvider = { scenarioRule ->
                // Por que `activity` começa NULA:
                // O ActivityScenario NÃO expõe a Activity por retorno direto. O
                // acesso é feito via callback `onActivity { ... }`, que entrega a
                // instância dentro do bloco. Como precisamos "extrair" esse valor
                // do callback para fora, declaramos uma var nula e a preenchemos
                // dentro do bloco. A nulidade aqui é só o estado INICIAL antes de o
                // callback rodar — não significa que a Activity não exista.
                var activity: FixingSettingsNavigationActivity? = null
                scenarioRule.scenario.onActivity { activity = it }

                // requireNotNull: o provider deve retornar uma Activity NÃO-nula.
                // onActivity só preenche `activity` se o scenario estiver num estado
                // válido (ao menos CREATED/RESUMED). Se, por algum motivo, o callback
                // não executar ou a Activity não existir, `activity` continuaria null.
                //
                // O que aconteceria se NÃO tratássemos a nulidade:
                // - O tipo esperado pelo activityProvider é a Activity (não-nula).
                //   Retornar um tipo anulável não compila sem o unwrap.
                // - Se forçássemos com `!!` e a Activity fosse null, estouraria um
                //   NullPointerException genérico, difícil de diagnosticar.
                // - O requireNotNull falha cedo e com MENSAGEM CLARA, apontando que
                //   o scenario não entregou a Activity (ex.: lançamento falhou,
                //   Activity não declarada no manifest, ou estado inválido).
                requireNotNull(activity) { "Activity não disponível no ActivityScenario" }
            }
        )

    @Test
    fun `given view intent when activity launches then navigates directly to settings`() {
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Ativar modo especial").assertIsDisplayed()
    }
}

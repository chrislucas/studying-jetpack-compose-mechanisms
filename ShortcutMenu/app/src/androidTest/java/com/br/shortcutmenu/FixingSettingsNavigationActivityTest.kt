package com.br.shortcutmenu

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Testa o fluxo de navegação da [FixingSettingsNavigationActivity] disparado
 * por uma intent de atalho (ACTION_VIEW), incluindo o caminho do onNewIntent.
 *
 * Usamos [createEmptyComposeRule] porque a Activity é lançada pelo
 * [ActivityScenario] (e não pelo rule). O rule vazio apenas conecta as
 * asserções de Compose à Activity que já está em execução.
 */
@RunWith(AndroidJUnit4::class)
class FixingSettingsNavigationActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    private val context: Context
        get() = ApplicationProvider.getApplicationContext()

    private fun viewIntent(): Intent =
        Intent(context, FixingSettingsNavigationActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }

    /**
     * Cenário onCreate: a Activity é aberta pela primeira vez já com a intent
     * ACTION_VIEW do atalho. O LaunchedEffect(intent) deve navegar para a tela
     * de Settings assim que o NavHost estiver composto.
     */
    @Test
    fun whenLaunchedWithViewIntent_thenNavigatesToSettings() {
        ActivityScenario.launch<FixingSettingsNavigationActivity>(viewIntent()).use {
            // O TopAppBar da tela de Settings exibe o título "Settings".
            composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        }
    }

    /**
     * Cenário onNewIntent: a Activity já existe (singleTop) e recebe uma nova
     * intent ACTION_VIEW — exatamente o caso de clicar no atalho com o app em
     * segundo plano.
     *
     * Como onNewIntent é protected, não o chamamos diretamente. Em vez disso,
     * disparamos novamente a mesma intent via Context.startActivity. Com a
     * Activity declarada como android:launchMode="singleTop", o Android reusa a
     * instância existente e entrega a intent pelo onNewIntent (em vez de recriar
     * a Activity), reproduzindo fielmente o fluxo do atalho.
     */
    @Test
    fun whenNewViewIntentArrives_thenNavigatesToSettings() {
        ActivityScenario.launch<FixingSettingsNavigationActivity>(viewIntent()).use {
            // Reentrega a intent do atalho; singleTop -> cai em onNewIntent.
            val reentryIntent = viewIntent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(reentryIntent)

            composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
        }
    }
}

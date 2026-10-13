package com.br.shortcutmenu

import androidx.compose.ui.semantics.Role

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertFalse

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun `given the state of switch is disabled, when checked then its value should be off`() {
        composeTestRule.setContent {
            SettingsScreen(false) { }
        }
        composeTestRule.onNodeWithRole(Role.Switch).assertIsOff()
    }

    @Test
    fun `given the state of switch is enabled, when checked then its value should be on`() {
        composeTestRule.setContent {
            SettingsScreen(true) { }
        }
        composeTestRule.onNodeWithRole(Role.Switch).assertIsOn()
    }


    @Test
    fun `given switch starts out enabled, when clicked then it should turn off`() {
        // Guarda o último valor emitido pelo callback onCheckedChange, apenas
        // para asserção extra ao final.
        var lastEmittedValue = true

        composeTestRule.setContent {
            // PROBLEMA (versão anterior):
            // Antes usávamos uma `var initValue = true` comum e passávamos ela
            // direto para SettingsScreen(initValue, ...). O Switch do Compose é
            // STATELESS (controlado): ele só exibe o que recebe no parâmetro
            // `checked` e NÃO guarda estado próprio. Ao clicar, ele apenas dispara
            // onCheckedChange — quem precisa atualizar o valor e disparar a
            // recomposição é o chamador.
            //
            // Uma `var` simples NÃO é observável pelo Compose: alterá-la dentro do
            // callback não agenda recomposição e o valor lido no setContent já foi
            // capturado uma única vez. Resultado: mesmo após o clique, o Switch
            // continuava com `checked = true` e o assertIsOff() falhava.
            //
            // SOLUÇÃO:
            // Usar mutableStateOf + remember cria um estado OBSERVÁVEL pelo Compose.
            // Quando o callback atualiza esse estado, o Compose recompõe
            // SettingsScreen com o novo valor de `checked`, refletindo o clique na
            // árvore semântica e fazendo o assertIsOff() passar.
            var isEnabled by remember { mutableStateOf(true) }

            SettingsScreen(isEnabled) { newState ->
                isEnabled = newState        // atualiza o estado observável -> recompõe
                lastEmittedValue = newState // guarda para a asserção final
            }
        }

        composeTestRule.onNodeWithRole(Role.Switch).assertIsOn()
        composeTestRule.onNodeWithRole(Role.Switch).performClick()
        composeTestRule.onNodeWithRole(Role.Switch).assertIsOff()

        assertFalse(lastEmittedValue)
    }
}
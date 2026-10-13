package com.br.shortcutmenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun given_the_state_of_switch_is_disabled_when_checked_then_its_value_should_be_off() {
        val state = false
        composeTestRule.setContent {
            SettingsScreen(state) { }
        }

        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Role, Role.Switch
            )
        ).assertIsOff()
    }

    @Test
    fun given_switch_start_out_with_a_state_when_clicked_on_it_then_it_toggles_its_value() {
        composeTestRule.setContent {
            var state by remember { mutableStateOf(false) }
            SettingsScreen(state) { state = it }
        }

        composeTestRule.onNode(
            SemanticsMatcher.expectValue(
                SemanticsProperties.Role,
                Role.Switch
            )
        )
            .assertIsOff()
            .performClick()
            .assertIsOn()
    }
}
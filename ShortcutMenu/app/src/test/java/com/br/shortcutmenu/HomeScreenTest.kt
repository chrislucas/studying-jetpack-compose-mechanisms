package com.br.shortcutmenu

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import kotlin.intArrayOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk =[34])
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun given_an_enabled_state_when_pass_it_through_component_then_text_ui_should_be_actived()  {
        composeTestRule.setContent {
            HomeScreen(isEnabled = true) { }
        }

        composeTestRule.onNodeWithText("Ativo").assertExists()
    }

    @Test
    fun given_a_disabled_state_when_pass_it_through_component_then_text_ui_should_be_inactived()  {
        composeTestRule.setContent {
            HomeScreen(isEnabled = false) { }
        }
        composeTestRule.onNodeWithText("Inativo").assertExists()
    }

    @Test
    fun given_a_button_on_layout_when_its_clicked_then_should_fire_lamnda_function_callback() {
        var wasFired = false
        composeTestRule.setContent {
            HomeScreen(isEnabled = true) { wasFired = true }
        }

        composeTestRule.onNodeWithText("Configurações").performClick()
        assertTrue(wasFired)
    }
}
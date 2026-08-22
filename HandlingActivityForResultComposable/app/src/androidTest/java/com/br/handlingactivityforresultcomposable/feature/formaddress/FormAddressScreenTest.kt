package com.br.handlingactivityforresultcomposable.feature.formaddress

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performTextInput
import com.br.handlingactivityforresultcomposable.R
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import com.br.handlingactivityforresultcomposable.feature.formaddress.ui.theme.HandlingActivityForResultComposableTheme
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class FormAddressScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fieldsAreDisplayed() {
        composeTestRule.setContent {
            HandlingActivityForResultComposableTheme {
                FormAddressScreen(onValidAddress = {})
            }
        }

        composeTestRule.onNodeWithText("Street").assertIsDisplayed()
        composeTestRule.onNodeWithText("Number").assertIsDisplayed()
    }

    @Test
    fun validForm_callsOnValidAddress() {
        var capturedAddress: Address? = null
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sendText = context.getString(R.string.send)

        composeTestRule.setContent {
            HandlingActivityForResultComposableTheme {
                FormAddressScreen(onValidAddress = { capturedAddress = it })
            }
        }

        // Enter valid data
        composeTestRule.onNodeWithText("Street").performTextReplacement("Main Street")
        composeTestRule.onNodeWithText("Number").performTextReplacement("123")

        // Click Send button
        composeTestRule.onNodeWithText(sendText).performClick()

        // Verify callback
        assertEquals("Main Street", capturedAddress?.street)
        assertEquals(123, capturedAddress?.number)
    }

    @Test
    fun invalidForm_doesNotCallOnValidAddress() {
        var capturedAddress: Address? = null
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sendText = context.getString(R.string.send)

        composeTestRule.setContent {
            HandlingActivityForResultComposableTheme {
                FormAddressScreen(onValidAddress = { capturedAddress = it })
            }
        }

        // Enter invalid data (empty street)
        composeTestRule.onNodeWithText("Street").performTextReplacement("")
        composeTestRule.onNodeWithText("Number").performTextReplacement("123")

        // Click Send button
        composeTestRule.onNodeWithText(sendText).performClick()

        // Verify callback NOT called
        assertNull(capturedAddress)
    }
}

package com.br.handlingactivityforresultcomposable.feature.formaddress

import android.app.Activity
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.br.handlingactivityforresultcomposable.R
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class FormAddressActivityTest {

    @get:Rule
    val composeTestRule = createEmptyComposeRule()

    @Test
    fun submittingValidForm_setsResultAndFinishes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val street = "Oxford Street"
        val number = 500
        val sendText = context.getString(R.string.send)

        ActivityScenario.launchActivityForResult(FormAddressActivity::class.java).use { scenario ->
            composeTestRule.onNodeWithText("Street").performTextReplacement(street)
            composeTestRule.onNodeWithText("Number").performTextReplacement(number.toString())

            composeTestRule.onNodeWithText(sendText).performClick()

            val result = scenario.result
            assertEquals(Activity.RESULT_OK, result.resultCode)

            val resultIntent = result.resultData
            val addressJson = resultIntent.getStringExtra(FormAddressActivity.EXTRA_ADDRESS)
            val address = Json.decodeFromString(Address.serializer(), addressJson!!)

            assertEquals(street, address.street)
            assertEquals(number, address.number)
        }
    }
}

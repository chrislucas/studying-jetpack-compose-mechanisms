package com.br.handlingactivityforresultcomposable.feature.formaddress

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import com.br.handlingactivityforresultcomposable.feature.formaddress.ui.theme.HandlingActivityForResultComposableTheme

class MainScreenScreenshotTest {
    @PreviewTest
    @Preview(showBackground = true, name = "Empty state")
    @Composable
    fun MainScreenEmpty() {
        HandlingActivityForResultComposableTheme {
            MainScreenContent()
        }
    }

    @PreviewTest
    @Preview(showBackground = true, name = "With Address")
    @Composable
    fun MainScreenWithAddress() {
        HandlingActivityForResultComposableTheme {
            MainScreenContent(
                address = Address(street = "Avenida Paulista", number = 1000)
            )
        }
    }
}

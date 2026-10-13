package com.br.composenav3.feature.navbydeeplink

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ProductDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun productDetailScreen_displaysProductDetails_whenProductIsNotNull() {
        val product = Product(id = "123", description = "Smartphone XYZ")
        val productState = MutableStateFlow<Product?>(product)

        composeTestRule.setContent {
            ProductDetailScreen(productState = productState)
        }

        composeTestRule.onNodeWithText("Smartphone XYZ").assertIsDisplayed()
        composeTestRule.onNodeWithText("ID: 123").assertIsDisplayed()
    }

    @Test
    fun productDetailScreen_displaysNotFoundMessage_whenProductIsNull() {
        val productState = MutableStateFlow<Product?>(null)

        composeTestRule.setContent {
            ProductDetailScreen(productState = productState)
        }

        composeTestRule.onNodeWithText("Produto não encontrado").assertIsDisplayed()
    }

    @Test
    fun productDetailScreen_updatesUI_whenStateChanges() {
        val productState = MutableStateFlow<Product?>(null)

        composeTestRule.setContent {
            ProductDetailScreen(productState = productState)
        }

        composeTestRule.onNodeWithText("Produto não encontrado").assertIsDisplayed()

        // Update state to valid product
        val product = Product(id = "456", description = "Laptop ABC")
        productState.value = product

        composeTestRule.onNodeWithText("Laptop ABC").assertIsDisplayed()
        composeTestRule.onNodeWithText("ID: 456").assertIsDisplayed()
    }
}

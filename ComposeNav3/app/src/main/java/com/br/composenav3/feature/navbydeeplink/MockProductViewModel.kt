package com.br.composenav3.feature.navbydeeplink

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn


data class ProductUIState(
    val products: List<Product> = emptyList(),
    val query: String = "",
)

class MockProductViewModel : ViewModel() {
    private val products = buildList {
        (100 .. 300).forEach {
            add(Product(it.toString(), "Product $it"))
        }
    }
    val searchTextFieldState = TextFieldState()

    fun getProductById(id: String): Product? = products.find { it.id == id }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val productUiState: StateFlow<ProductUIState> =
        snapshotFlow { searchTextFieldState.text.toString() }
            .debounce(500L.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    MutableStateFlow(ProductUIState(products = products))
                } else {
                    val filteredProducts = products.filter {
                        it.description.contains(
                            query,
                            ignoreCase = true
                        )
                    }
                    MutableStateFlow(ProductUIState(products = filteredProducts, query = query))
                }
            }.stateIn(viewModelScope, SharingStarted.Lazily, ProductUIState())
}

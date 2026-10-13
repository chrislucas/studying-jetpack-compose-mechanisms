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

    /**
     * Expõe o produto selecionado como um StateFlow, resolvendo a busca fora da
     * composição (uma única vez, ao criar o fluxo) em vez de a cada recomposição.
     * Segue o mesmo padrão reativo de productUiState e mantém a UI desacoplada da
     * forma como o produto é obtido. Caso a busca passe a ser cara (I/O, rede),
     * basta trocar a implementação interna sem alterar a assinatura nem a UI.
     */
    fun productDetailState(id: String): StateFlow<Product?> =
        MutableStateFlow(getProductById(id))

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val productUiState: StateFlow<ProductUIState> =
        // snapshotFlow converte o estado observável do Compose (searchTextFieldState.text)
        // em um Flow do Kotlin coroutines. Cada vez que o texto digitado muda, o bloco é
        // reavaliado e o novo valor (a String da query) é emitido para o restante do pipeline.
        snapshotFlow { searchTextFieldState.text.toString() }
            // debounce aguarda 500ms de "silêncio" (nenhuma nova emissão) antes de deixar o
            // último valor passar. Enquanto o usuário continua digitando rapidamente, as
            // emissões intermediárias são descartadas. Isso evita filtrar a lista a cada tecla
            // pressionada, reduzindo trabalho desnecessário e melhorando a performance.
            .debounce(500L.milliseconds)
            // distinctUntilChanged só deixa passar um valor se ele for diferente do anterior.
            // Protege contra reprocessamento quando a query final é igual à última já processada
            // (por exemplo, digitar e apagar voltando ao mesmo texto), evitando recomputar o
            // mesmo resultado.
            .distinctUntilChanged()
            // flatMapLatest troca o fluxo de uma query String por um novo Flow que produz o
            // ProductUIState correspondente. O "Latest" é o ponto chave: se uma nova query chega
            // enquanto o processamento da anterior ainda não terminou, o Flow anterior é
            // cancelado e substituído pelo novo, garantindo que apenas o resultado da query mais
            // recente seja considerado (sem resultados obsoletos/fora de ordem).
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    // Query vazia: retorna a lista completa de produtos, sem filtro.
                    MutableStateFlow(ProductUIState(products = products))
                } else {
                    // Query preenchida: filtra os produtos cuja descrição contém o texto buscado
                    // (ignorando maiúsculas/minúsculas) e mantém a query no estado.
                    val filteredProducts = products.filter {
                        it.description.contains(
                            query,
                            ignoreCase = true
                        )
                    }
                    MutableStateFlow(ProductUIState(products = filteredProducts, query = query))
                }
            }
            // stateIn converte o Flow frio resultante em um StateFlow quente, com cache do último
            // valor, compartilhado entre todos os coletores (ex.: recomposições/rotações da UI).
            // - viewModelScope: o fluxo vive atrelado ao ciclo de vida do ViewModel.
            // - SharingStarted.Lazily: a coleta inicia no primeiro coletor e permanece ativa até
            //   o escopo ser cancelado (não para quando a UI deixa de observar temporariamente).
            // - ProductUIState(): valor inicial emitido antes do primeiro resultado do pipeline.
            .stateIn(viewModelScope, SharingStarted.Lazily, ProductUIState())
}

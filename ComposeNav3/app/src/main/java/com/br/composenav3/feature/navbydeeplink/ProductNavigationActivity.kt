package com.br.composenav3.feature.navbydeeplink

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.metadata
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.br.composenav3.feature.navbydeeplink.ui.theme.ComposeNav3Theme


class NavByDeeplinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNav3Theme {
                /*
                    rememberNavBackStack persiste a pilha de navegação entre config changes
                    (rotação de tela, etc). Requer que as NavKeys sejam @Serializable.
                 */
                val backStack = rememberNavBackStack(HomeKey)

                // Trata deep link na abertura da Activity
                handleDeepLink(intent, backStack)

                NavDisplay(
                    backStack = backStack,
                    onBack = {
                        // Remove o último elemento da lista ao voltar.
                        if (backStack.size > 1) {
                            backStack.removeLast()
                        } else {
                            finish() // Fecha o app se estiver na Home (última tela restante)
                        }
                    },
                    entryDecorators = listOf(
                        // Mantém o estado de rememberSaveable() para cada entry
                        rememberSaveableStateHolderNavEntryDecorator(),
                        // Gerencia o ciclo de vida da ViewModel para cada entry
                        // (ViewModel é destruída ao remover a entry do backStack)
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        entry<HomeKey> {
                            /*
                                Animacao local
                             */

                            metadata {
                                /*
                                    override do transitionSpec para essa entry
                                     entra deslizando debaixo para cima/
                                 */

                                put(NavDisplay.TransitionKey) {
                                    slideInHorizontally(initialOffsetX = { it }) togetherWith
                                            ExitTransition.KeepUntilTransitionsFinished
                                }

                                /*
                                    override do popTransitionSpec
                                    // Sai deslizando para baixo
                                 */

                                put(NavDisplay.PopTransitionKey) {
                                    EnterTransition.None togetherWith slideOutVertically { it }
                                }


                                /*
                                    override do predictivePopTransitionSpec
                                 */

                                put(NavDisplay.PredictivePopTransitionKey) {
                                    EnterTransition.None togetherWith slideOutVertically { it }
                                }
                            }

                            HomeScreen(
                                onNavigateToProducts = dropUnlessResumed {
                                    backStack.add(ProductListKey)
                                })
                        }

                        entry<ProductListKey> {
                            /*
                                ViewModel com escopo na Activity: a mesma instância é
                                compartilhada entre ProductList e ProductDetail.
                                Simula um repository que carrega a lista uma vez e a
                                mantém em memória enquanto a Activity existir.
                             */
                            val sharedViewModel: MockProductViewModel = viewModel(
                                viewModelStoreOwner = this@NavByDeeplinkActivity
                            )
                            ProductListScreen(
                                viewModel = sharedViewModel, onProductClick = { productId ->
                                    backStack.add(ProductDetailKey(productId))
                                })
                        }

                        entry<ProductDetailKey> { productDetail ->
                            val sharedViewModel: MockProductViewModel = viewModel(
                                viewModelStoreOwner = this@NavByDeeplinkActivity
                            )
                            ProductDetailScreen(
                                productId = productDetail.productId, viewModel = sharedViewModel
                            )
                        }
                    },
                    /*
                        Global Animation
                     */

                    // animacao ao adicionar na stack
                    transitionSpec = {
                        slideInHorizontally(initialOffsetX = { it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { -it })
                    },

                    popTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    },

                    /*
                        Animacao de predictive back (swipe para voltar)
                     */

                    predictivePopTransitionSpec = {
                        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                                slideOutHorizontally(targetOffsetX = { it })
                    }
                )
            }
        }
    }

    /**
     * Chamado quando a Activity já existe em background e recebe um novo deep link.
     */
    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)
        // A recomposição será acionada pelo novo intent
    }
}

@Composable
fun HomeScreen(onNavigateToProducts: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onNavigateToProducts, shape = RectangleShape
        ) {
            Text(text = "Go to Product List")
        }
    }
}

@Composable
fun ProductListScreen(
    viewModel: MockProductViewModel, onProductClick: (String) -> Unit
) {
    val uiState by viewModel.productUiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Surface(tonalElevation = 3.dp) {
                OutlinedTextField(
                    state = viewModel.searchTextFieldState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    placeholder = { Text("Filter products") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search icon")
                    })
            }
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(uiState.products, key = { it.id }) { product ->
                ProductListItem(
                    product = product, onClick = { onProductClick(product.id) })
            }
        }
    }
}

@Composable
private fun ProductListItem(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}


@Composable
fun ProductDetailScreen(productId: String, viewModel: MockProductViewModel) {
    val product = viewModel.getProductById(productId)

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (product != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = product.description, style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ID: ${product.id}", style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Text(
                    text = "Produto não encontrado (id: $productId)",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    ComposeNav3Theme {
        HomeScreen(onNavigateToProducts = {})
    }
}

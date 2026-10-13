package com.br.composenav3.feature.navbydeeplink

import android.app.ComponentCaller
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.remember
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
                            backStack.removeAt(backStack.lastIndex)
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
                            // remember garante que o StateFlow seja criado uma única vez
                            // para este productId, evitando recriá-lo a cada recomposição.
                            val productState = remember(productDetail.productId) {
                                sharedViewModel.productDetailState(productDetail.productId)
                            }
                            ProductDetailScreen(productState = productState)
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
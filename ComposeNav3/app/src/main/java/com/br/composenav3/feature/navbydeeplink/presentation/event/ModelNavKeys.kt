package com.br.composenav3.feature.navbydeeplink

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/*
    Navigation 3 usa objetos de dados puros como chaves de navegação (NavKey).
    Para que o backstack persista entre configuration changes (rotação, etc),
    as chaves devem ser @Serializable e implementar NavKey.
 */

@Serializable
data object HomeKey : NavKey

@Serializable
data object ProductListKey : NavKey

@Serializable
data class ProductDetailKey(val productId: String) : NavKey




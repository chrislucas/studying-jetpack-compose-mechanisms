package com.br.composenav3.feature.navbydeeplink

import android.content.Intent
import android.net.Uri
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Trata o deep link, adicionando a tela alvo ao backstack se necessário.
 * Previne readicionar a mesma tela em recomposições.
 */
fun handleDeepLink(intent: Intent?, backStack: NavBackStack<NavKey>) {
    val uri = intent?.data ?: return
    val target = parseDeeplink(uri) ?: return

    // Evita adicionar duplicata se já está no topo
    if (backStack.lastOrNull() != target) {
        backStack.add(target)
    }
}

/*
    Parsing de deep links:
    - Extrai os segmentos da URL e mapeia para a NavKey correspondente.
    - Retorna null se a URL não for reconhecida.
 */
private fun parseDeeplink(uri: Uri): NavKey? {
    if (uri.host != "navbydeeplink.com") return null

    val pathSegments = uri.pathSegments
    return when {
        pathSegments.isEmpty() -> HomeKey
        pathSegments.firstOrNull() == "products" -> ProductListKey

        // navbydeeplink.com/product/123456
        pathSegments.firstOrNull() == "product" -> {
            val id = pathSegments.getOrNull(1)
            id?.let { ProductDetailKey(it) }
        }

        else -> null
    }
}
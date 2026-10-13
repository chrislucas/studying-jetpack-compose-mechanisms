package com.br.navigation

/**
 * Monta as URLs de deep link a partir de um [AppDestination].
 *
 * É o inverso do [DeepLinkParser]. O [:app] usa esta fábrica para construir a
 * `Uri` que vai dentro do `Intent(ACTION_VIEW)` ao navegar para outro módulo.
 *
 * Mantida pura (retorna `String`) para facilitar o teste de unidade na JVM.
 */
object DeepLinkFactory {

    private const val base = "${DeepLinkParser.SCHEME}://${DeepLinkParser.HOST}"

    fun urlFor(destination: AppDestination): String = when (destination) {
        is AppDestination.Wallet -> "$base/${DeepLinkParser.PATH_WALLET}"

        is AppDestination.Transfer -> {
            val query = buildList {
                destination.amount?.let { add("${DeepLinkParser.PARAM_AMOUNT}=$it") }
                destination.toAccount?.let { add("${DeepLinkParser.PARAM_TO_ACCOUNT}=$it") }
            }
            val path = "$base/${DeepLinkParser.PATH_TRANSFER}"
            if (query.isEmpty()) path else "$path?${query.joinToString("&")}"
        }
    }
}

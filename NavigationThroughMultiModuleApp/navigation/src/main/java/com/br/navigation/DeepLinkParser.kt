package com.br.navigation

/**
 * Converte URLs de deep link (ex.: `app://library/transfer?amount=10&toAccount=ACC-2`)
 * em [AppDestination]s e vice-versa.
 *
 * Implementação proposital em Kotlin/JVM puro (sem `android.net.Uri`) para que
 * possa ser testada por unidade na JVM, de forma rápida e sem Robolectric.
 * No lado Android, a Activity apenas passa `intent.data?.toString()` para cá.
 *
 * Scheme: `app`  •  Host: `library`
 */
object DeepLinkParser {

    const val SCHEME = "app"
    const val HOST = "library"

    const val PATH_WALLET = "wallet"
    const val PATH_TRANSFER = "transfer"

    const val PARAM_AMOUNT = "amount"
    const val PARAM_TO_ACCOUNT = "toAccount"

    private val base = "$SCHEME://$HOST"

    /**
     * Faz o parse de uma URL de deep link para um [AppDestination].
     *
     * @return o destino correspondente ou `null` se a URL for nula, de outro
     * scheme/host, ou apontar para um caminho desconhecido.
     */
    fun parse(url: String?): AppDestination? {
        if (url.isNullOrBlank()) return null
        if (!url.startsWith("$base/")) return null

        val remainder = url.removePrefix("$base/")
        val path = remainder.substringBefore('?')
        val query = remainder.substringAfter('?', missingDelimiterValue = "")
        val params = parseQuery(query)

        return when (path) {
            PATH_WALLET -> AppDestination.Wallet
            PATH_TRANSFER -> AppDestination.Transfer(
                amount = params[PARAM_AMOUNT]?.toDoubleOrNull(),
                toAccount = params[PARAM_TO_ACCOUNT],
            )
            else -> null
        }
    }

    private fun parseQuery(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        return query.split('&')
            .mapNotNull { pair ->
                val idx = pair.indexOf('=')
                if (idx <= 0) return@mapNotNull null
                val key = decode(pair.substring(0, idx))
                val value = decode(pair.substring(idx + 1))
                key to value
            }
            .toMap()
    }

    private fun decode(s: String): String =
        s.replace("%20", " ").replace('+', ' ')
}

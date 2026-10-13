package com.br.navigation

/**
 * Destinos de nível de aplicativo que podem ser alcançados por deep link
 * entre Activities de módulos diferentes.
 *
 * Cada feature module é a "casa" de um destes destinos. O [:app] usa estes
 * destinos para montar o Intent de deep link (ver [DeepLinkFactory]), e cada
 * feature Activity usa o [DeepLinkParser] para transformar o Intent recebido
 * de volta em um destes destinos.
 *
 * Observação didática: mantemos este contrato no módulo de navegação (puro)
 * para que o :app não precise depender diretamente dos feature modules apenas
 * para saber "quais deep links existem".
 */
sealed interface AppDestination {

    /** Carteira do usuário. Deep link: app://library/wallet */
    data object Wallet : AppDestination

    /**
     * Fluxo de transferência, opcionalmente pré-preenchido.
     * Deep link: app://library/transfer?amount=123.45&toAccount=ACC-2
     *
     * Demonstra como passar informação de uma Activity para outra via deep link.
     */
    data class Transfer(
        val amount: Double? = null,
        val toAccount: String? = null,
    ) : AppDestination
}

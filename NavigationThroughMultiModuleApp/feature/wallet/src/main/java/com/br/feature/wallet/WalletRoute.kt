package com.br.feature.wallet

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rotas internas (NavKeys) do módulo Carteira.
 *
 * Vivem no back stack do [com.br.navigation.Navigator] deste módulo e são
 * usadas pelo `NavDisplay`. São `@Serializable` para sobreviver a mudanças de
 * configuração/morte de processo.
 *
 * [AccountDetailRoute] demonstra como passar informação **entre composables**
 * via Navigation 3: o `accountId` é um argumento da própria rota.
 */
sealed interface WalletRoute : NavKey {

    /** Menu inicial do módulo (primeira tela do entrypoint). */
    @Serializable
    data object Menu : WalletRoute

    /** Lista de contas da carteira. */
    @Serializable
    data object Accounts : WalletRoute

    /** Detalhe de uma conta específica — recebe o id via argumento de rota. */
    @Serializable
    data class AccountDetail(val accountId: String) : WalletRoute
}

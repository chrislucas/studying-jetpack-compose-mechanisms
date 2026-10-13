package com.br.feature.transfer

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Rotas internas (NavKeys) do módulo Transferência.
 *
 * O fluxo é: [Form] → [Confirmation] → [Receipt].
 *
 * - [Form] pode ser pré-preenchida com dados vindos **de outra Activity via
 *   deep link** (amount/toAccount na URL).
 * - [Confirmation] recebe os valores **de outro composable via Navigation 3**
 *   (argumentos da rota).
 */
sealed interface TransferRoute : NavKey {

    /**
     * Formulário de transferência. Os campos podem vir pré-preenchidos por deep
     * link (ex.: um app externo sugere "transferir R$ 50 para ACC-2").
     */
    @Serializable
    data class Form(
        val prefilledAmount: Double? = null,
        val prefilledToAccount: String? = null,
    ) : TransferRoute

    /** Confirmação: recebe os dados escolhidos no formulário. */
    @Serializable
    data class Confirmation(
        val amount: Double,
        val toAccount: String,
    ) : TransferRoute

    /** Recibo final após confirmar. */
    @Serializable
    data class Receipt(
        val amount: Double,
        val toAccount: String,
    ) : TransferRoute
}

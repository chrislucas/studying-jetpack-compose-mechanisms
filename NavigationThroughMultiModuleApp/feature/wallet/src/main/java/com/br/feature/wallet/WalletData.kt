package com.br.feature.wallet

/** Modelo simples de conta usado para fins didáticos. */
data class Account(
    val id: String,
    val name: String,
    val balance: Double,
)

/** Fonte de dados em memória (sem rede/DB para manter o foco em navegação). */
object WalletData {
    val accounts: List<Account> = listOf(
        Account(id = "ACC-1", name = "Conta Corrente", balance = 2540.75),
        Account(id = "ACC-2", name = "Poupança", balance = 10320.00),
        Account(id = "ACC-3", name = "Investimentos", balance = 8750.42),
    )

    fun byId(id: String): Account? = accounts.firstOrNull { it.id == id }
}

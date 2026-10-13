package com.br.feature.wallet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Telas (composables) do módulo Carteira.
 *
 * Todas usam a API de `semantics` do Compose para acessibilidade:
 * - `heading()` marca títulos para que leitores de tela os anunciem como cabeçalho;
 * - `contentDescription` descreve o conteúdo agregado de itens/cliques;
 * - `semantics(mergeDescendants = true)` agrupa um item clicável em um único nó.
 */

/** Tela 1: menu do módulo (primeira tela do entrypoint). */
@Composable
fun WalletMenuScreen(
    onOpenAccounts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Carteira",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text("Gerencie suas contas e saldos.")

        Button(
            onClick = onOpenAccounts,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Abrir lista de contas" },
        ) {
            Text("Minhas contas")
        }
    }
}

/** Tela 2: lista de contas; clicar navega para o detalhe passando o accountId. */
@Composable
fun AccountsScreen(
    accounts: List<Account>,
    onAccountClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = "Minhas contas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .semantics { heading() },
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(accounts, key = { it.id }) { account ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAccountClick(account.id) }
                        .semantics(mergeDescendants = true) {
                            contentDescription =
                                "Conta ${account.name}, saldo ${formatBrl(account.balance)}. " +
                                "Toque para ver detalhes."
                        },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(account.name, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(formatBrl(account.balance))
                    }
                }
            }
        }
    }
}

/** Tela 3: detalhe de uma conta. Recebe o id via argumento da rota. */
@Composable
fun AccountDetailScreen(
    account: Account?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (account == null) {
            Text(
                text = "Conta não encontrada",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )
        } else {
            Text(
                text = account.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = "Saldo disponível: ${formatBrl(account.balance)}",
                modifier = Modifier.semantics {
                    contentDescription = "Saldo disponível ${formatBrl(account.balance)}"
                },
            )
            Text("Identificador: ${account.id}")
        }

        Button(
            onClick = onBack,
            modifier = Modifier.semantics { contentDescription = "Voltar para a lista de contas" },
        ) {
            Text("Voltar")
        }
    }
}

/** Formata um valor monetário simples em reais (didático, sem Locale completo). */
internal fun formatBrl(value: Double): String =
    "R$ " + "%,.2f".format(value)

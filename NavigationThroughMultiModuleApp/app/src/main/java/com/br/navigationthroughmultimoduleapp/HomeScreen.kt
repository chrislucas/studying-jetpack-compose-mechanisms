package com.br.navigationthroughmultimoduleapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Tela inicial do :app — um menu que leva aos dois feature modules.
 *
 * Os cliques NÃO navegam dentro do :app: eles disparam deep links (Intents
 * implícitos) que são resolvidos pelo Android para a Activity de cada módulo.
 * Essa é a demonstração central de navegação entre Activities de módulos
 * diferentes via deep link.
 *
 * Separado da Activity para ser testável por UI (Robolectric + Compose).
 */
@Composable
fun HomeScreen(
    onOpenWallet: () -> Unit,
    onOpenTransfer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Meu Banco",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.semantics { heading() },
        )
        Text("Escolha um recurso para abrir via deep link.")

        Button(
            onClick = onOpenWallet,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Abrir Carteira via deep link" },
        ) {
            Text("Carteira")
        }

        Button(
            onClick = onOpenTransfer,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Abrir Transferência via deep link" },
        ) {
            Text("Transferência")
        }
    }
}

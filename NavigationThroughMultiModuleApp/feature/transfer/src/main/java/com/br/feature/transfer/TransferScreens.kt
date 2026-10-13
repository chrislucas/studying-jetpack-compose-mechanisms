package com.br.feature.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Telas do módulo Transferência, todas acessíveis via API de `semantics`.
 */

/**
 * Tela 1: formulário. Pode ser pré-preenchida por deep link.
 * Ao avançar, entrega os valores digitados para a próxima rota (confirmação).
 */
@Composable
fun TransferFormScreen(
    prefilledAmount: Double?,
    prefilledToAccount: String?,
    onContinue: (amount: Double, toAccount: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var amountText by rememberSaveable {
        mutableStateOf(prefilledAmount?.toString().orEmpty())
    }
    var toAccount by rememberSaveable {
        mutableStateOf(prefilledToAccount.orEmpty())
    }

    val amount = amountText.toDoubleOrNull()
    val isValid = amount != null && amount > 0.0 && toAccount.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Nova transferência",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Valor") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de valor da transferência" },
        )

        OutlinedTextField(
            value = toAccount,
            onValueChange = { toAccount = it },
            label = { Text("Conta de destino") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Campo de conta de destino" },
        )

        Button(
            onClick = { if (isValid) onContinue(amount!!, toAccount) },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Continuar para a confirmação" },
        ) {
            Text("Continuar")
        }
    }
}

/** Tela 2: confirmação. Recebe amount/toAccount do composable anterior. */
@Composable
fun TransferConfirmationScreen(
    amount: Double,
    toAccount: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Confirmar transferência",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "Você vai transferir ${formatBrl(amount)} para a conta $toAccount.",
            modifier = Modifier.semantics {
                contentDescription =
                    "Transferir ${formatBrl(amount)} para a conta $toAccount"
            },
        )

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Confirmar transferência" },
        ) {
            Text("Confirmar")
        }
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Voltar e editar a transferência" },
        ) {
            Text("Voltar")
        }
    }
}

/** Tela 3: recibo final. */
@Composable
fun TransferReceiptScreen(
    amount: Double,
    toAccount: String,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Transferência concluída",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = "${formatBrl(amount)} enviados para $toAccount.",
            modifier = Modifier.semantics {
                contentDescription =
                    "${formatBrl(amount)} enviados para a conta $toAccount"
            },
        )
        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Concluir e fechar" },
        ) {
            Text("Concluir")
        }
    }
}

internal fun formatBrl(value: Double): String =
    "R$ " + "%,.2f".format(value)

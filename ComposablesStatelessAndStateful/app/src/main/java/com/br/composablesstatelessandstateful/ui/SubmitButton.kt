package com.br.composablesstatelessandstateful.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SubmitButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(onClick = onClick, enabled = isEnabled) { Text("Enviar") }
}
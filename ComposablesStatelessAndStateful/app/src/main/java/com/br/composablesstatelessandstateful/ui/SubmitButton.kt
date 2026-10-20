package com.br.composablesstatelessandstateful.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SubmitButton(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(onClick = onClick, enabled = isEnabled, modifier = modifier) { Text("Enviar") }
}
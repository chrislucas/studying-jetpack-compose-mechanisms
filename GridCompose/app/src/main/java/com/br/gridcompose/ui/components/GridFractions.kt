package com.br.gridcompose.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridFractionApi() {
    Box(modifier = Modifier.fillMaxSize()) {
        Grid(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            config = {
                // 1. Registra 3 colunas de forma sequencial
                column(1.fr)
                column(1.fr)
                column(1.fr)

                // 2. Registra 3 linhas de forma sequencial
                row(1.fr)
                row(1.fr)
                row(1.fr)

                gap(12.dp)
            }
        ) {
            Box(modifier = Modifier.gridItem(rowSpan = 2)) {
                // Conteúdo do item 1
                Text("Item 1")
            }
            Box(modifier = Modifier.gridItem(columnSpan = 2)) {
                // Conteúdo do item 2
                Text("Item 2")
            }
        }
    }
}
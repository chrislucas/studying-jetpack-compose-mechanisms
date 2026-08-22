package com.br.gridcompose.ui.components

import android.R.attr.track
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridScope
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.columns
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.rows
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/*
    https://share.google/aimode/Knel5ak6FZJsv7bIc
 */

@OptIn(ExperimentalGridApi::class)
@Preview(showBackground = true)
@Composable
fun SimpleGridComponent() {
    Grid(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        config = {
            /*
                https://developer.android.com/blog/posts/whats-new-in-the-jetpack-compose-april-26-release?hl=pt-br
             */
            repeat(4) {
                columns(GridTrackSize.Fixed(.25f.dp))
            }

            repeat(2) {
                rows(GridTrackSize.Fixed(.5f.dp))
            }

            gap(16.dp)
        }
    ) {
        Card(modifier = Modifier.gridItem(rowSpan = 2)) {
            Text("Item 1")
        }
        Card(modifier = Modifier.gridItem(columnSpan = 3)) {
            Text("Item 2")
        }
        Card(modifier = Modifier.gridItem(columnSpan = 2)) { }
    }
}

@OptIn(ExperimentalGridApi::class)
@Preview(showBackground = true)
@Composable
fun CompleteGrid() {
    Grid(
        config = {
            repeat(4) { column(0.25f) }
            repeat(2) { row(0.5f) }
            gap(16.dp)
        }
    ) {
        SimpleScreen()
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun AnotherCompleteGrid() {
    Grid(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        config = {
            area("conteudo", row = 0, column = 0, rowSpan = 1, columnSpan = 1)

            repeat(4) {
                columns(GridTrackSize.Flex(1.fr))
            }

            repeat(2) {
                rows(GridTrackSize.Flex(1.fr))
            }

            gap(12.dp)
        }
    ) {
        SimpleScreen()
    }
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun GridScope.SimpleScreen() {
    Box(
        modifier = Modifier
            .gridItem(columnSpan = 2)
            .background(Color(0xFF3F51B5)),
        contentAlignment = Alignment.Center
    ) {
        Text("Largura Dupla (columnSpan = 2)", color = Color.White)
    }

    // Item 2: Item normal (1x1)
    Box(
        modifier = Modifier.background(Color(0xFFE91E63)),
        contentAlignment = Alignment.Center
    ) {
        Text("Normal 1:", color = Color.White)
    }

    // Item 3: Ocupa 2 linhas de altura (rowSpan = 2)
    Box(
        modifier = Modifier
            .gridItem(rowSpan = 2)
            .background(Color(0xFF4CAF50)),
        contentAlignment = Alignment.Center
    ) {
        Text("Altura Dupla (rowSpan = 2)", color = Color.White)
    }

    // Item 4: Item normal (1x1)
    Box(
        modifier = Modifier.background(Color(0xFFFF9800)),
        contentAlignment = Alignment.Center
    ) {
        Text("Normal 2", color = Color.White)
    }

    // Item 5: Item normal (1x1)
    Box(
        modifier = Modifier.background(Color(0xFF9C27B0)),
        contentAlignment = Alignment.Center
    ) {
        Text("Normal 3", color = Color.White)
    }

    // Item 6: Ocupa 2 colunas de largura e 1 de altura
    Box(
        modifier = Modifier
            .gridItem(columnSpan = 2)
            .background(Color(0xFF00BCD4)),
        contentAlignment = Alignment.Center
    ) {
        Text("Base Larga (columnSpan = 2)", color = Color.White)
    }
}
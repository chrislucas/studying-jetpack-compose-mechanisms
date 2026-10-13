package com.br.composenav3.feature.navbydeeplink

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import com.br.composenav3.feature.navbydeeplink.ui.theme.ComposeNav3Theme

@Composable
fun HomeScreen(onNavigateToProducts: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(
            onClick = onNavigateToProducts, shape = RectangleShape
        ) {
            Text(text = "Go to Product List")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    ComposeNav3Theme {
        HomeScreen(onNavigateToProducts = {})
    }
}

package com.br.composablesstatelessandstateful

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.composablesstatelessandstateful.ui.stateful.StateBasedStatefulLoginScreen
import com.br.composablesstatelessandstateful.ui.stateful.ValueBasedStatefulLoginScreen
import com.br.composablesstatelessandstateful.ui.theme.ComposablesStatelessAndStatefulTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposablesStatelessAndStatefulTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormComponent(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FormComponent(modifier: Modifier = Modifier) {
    /*
        ValueBasedStatefulLoginScreen(modifier = modifier)
     */
    StateBasedStatefulLoginScreen(modifier = modifier)
}
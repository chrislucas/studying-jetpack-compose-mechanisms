package com.br.composelifecyclestufies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.composelifecyclestufies.ui.theme.ComposeLifecycleStufiesTheme

/*
    https://gemini.google.com/app/8f0afba8eac2698f

    Gostaria de implementar um projeto em jetpack compose android que explore os conceitos


    Projeto baseado no artigo
    https://medium.com/@sivavishnu0705/three-moments-in-a-composables-life-composition-recomposition-and-measure-time-subcomposition-e920d5ab3386

    - Os 3 conceitos
        - Composition
        - Recomposition
        - Measure time SubComposition

 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLifecycleStufiesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeLifecycleStufiesTheme {
        Greeting("Android")
    }
}
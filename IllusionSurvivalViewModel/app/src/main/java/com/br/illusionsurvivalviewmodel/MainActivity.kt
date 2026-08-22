package com.br.illusionsurvivalviewmodel

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
import com.br.illusionsurvivalviewmodel.ui.theme.IllusionSurvivalViewModelTheme

/*
    Esse projeto serve para explorar os limites de uma viewmodel através
    dos seguintes artigos:
    
    https://medium.com/@sivavishnu0705/part-1-the-illusion-of-survival-why-your-viewmodels-are-lying-to-you-d66e8467d4c3
    https://medium.com/@sivavishnu0705/part-2-the-direction-of-truth-references-replays-and-the-inversion-of-control-in-android-uis-55649e1394b3
    https://medium.com/@sivavishnu0705/part-3-scope-is-a-security-boundary-leaking-lifetimes-and-account-bleeding-in-android-5c01c4ab7f03
    https://medium.com/@sivavishnu0705/part-4-cold-flows-hot-bugs-stabilizing-upstream-pipelines-against-compose-recomposition-9d2ed56f840f
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IllusionSurvivalViewModelTheme {
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
    IllusionSurvivalViewModelTheme {
        Greeting("Android")
    }
}
package com.br.nav2problemnavigation

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
import com.br.nav2problemnavigation.ui.theme.Nav2ProblemNavigationTheme

/*
    Gostaria de explorar um exemplo sobre os contras do uso de Nav2 para ver na prática,
    como Falta de controle direto sobre o histórico e a Dificuldade crônica para lidar de forma
    elegante com designs multi-pane com um projeto prático em jetpack compose para android.

    Crie um tema e me de o código passo a passo para explorar essa questão.

    A arquitetura padrao do Navigation COmpose (Nav2) baseia-se em um modelo linear
    de pilha e rotas orientadas a strings. Para um dev android lidando com UI
    escalavel e testes de acessibildiade, isso cria gargalos estruturais severos
    quando o aplicativo precisa de comporamentos que nao sejam uma navegacao
    direta de A para B


 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nav2ProblemNavigationTheme {
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
    Nav2ProblemNavigationTheme {
        Greeting("Android")
    }
}
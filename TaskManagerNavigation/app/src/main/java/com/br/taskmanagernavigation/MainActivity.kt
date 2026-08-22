package com.br.taskmanagernavigation

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
import com.br.taskmanagernavigation.ui.theme.TaskManagerNavigationTheme


/*
    3. Gerenciador de Tarefas com Notificações
    Perfeito para dominar o gerenciamento do estado em aplicativos orientados a eventos e interações externas.

    •	O fluxo do app: Dashboard de produtividade e um formulário rápido de edição de pendências.

    •	O cenário de teste: Uma notificação push ou um Widget de tela inicial
    dispara diretamente a tela de edição da tarefa.

    •	O desafio Nav3: Dominar o Task Stack Management. Você criará um DeepLinkMatcher customizado
    para ler dados diretamente dos Extras da notificação e programará a
    recriação correta do estado do app quando a ação "Up" for acionada

 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerNavigationTheme {
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
    TaskManagerNavigationTheme {
        Greeting("Android")
    }
}
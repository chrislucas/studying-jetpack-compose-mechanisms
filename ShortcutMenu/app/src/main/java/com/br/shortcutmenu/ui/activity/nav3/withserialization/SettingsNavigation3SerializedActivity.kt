package com.br.shortcutmenu.ui.activity.nav3.withserialization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.br.shortcutmenu.ui.theme.ShortcutMenuTheme
import kotlinx.serialization.Serializable


@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Home : Screen

    // Exemplo de tela que recebe parâmetros com segurança de tipos
    @Serializable
    data class Settings(val categoryId: Int = 0) : Screen
}

/*
    O exemplo de navegação não utiliza sealed interface com serialização, por que?
    Quando utilizar esse recurso?
 */
class SettingsNavigation3SerializedActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShortcutMenuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ShortcutMenuTheme {
        Greeting2("Android")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nav3HomeScreen(isEnabled: Boolean, navToSettings: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Home") })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isEnabled) "Ativo" else "Inativo",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navToSettings() }) {
                Text("Configurações")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Nav3SettingsScreen(isEnabled: Boolean, onChangeValue: (Boolean) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") })
    }) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ativar modo especial",
                style = MaterialTheme.typography.headlineMedium
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onChangeValue
            )
        }
    }
}
package com.br.shortcutmenu.ui.activity.nav3.simple

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
import androidx.compose.ui.unit.dp
import com.br.shortcutmenu.ui.theme.ShortcutMenuTheme

/*
    Como modificar a navegação para navigation component 3

    Aqui teremos uma mudanca completa de paraigma para o ecossistema android.
    Ao inves de usar uma maquina de estados oculta com navController e rotas baseadas em
    Strings, o Nav3 é baseado em State-Driven Architecture (Arquitetura orientada a
    estados).

    - Nesse modelo passamos a ser dono da pilha de telas (Back stack), que passa a ser
    representada por uma lista mutavel (MutbleList), contendo objetos fortemente tipados.
 */


sealed interface Screen {
    object Home : Screen
    object Settings : Screen
}
class SettingsNavigation3Activity : ComponentActivity() {

    private val backStackScreen = mutableListOf<Screen>(Screen.Home)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShortcutMenuTheme {

            }
        }
    }
}


/*
    Novas funcoes composable porque o tratamento de navegacao pela barra na TopBar
    é diferente entre Nav2 e Nav3
 */

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
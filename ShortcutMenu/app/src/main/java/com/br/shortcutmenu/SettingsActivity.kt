package com.br.shortcutmenu

import android.content.Intent
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.startup.AppInitializer
import com.br.shortcutmenu.core.SettingRepositoryInitializer
import com.br.shortcutmenu.ui.theme.ShortcutMenuTheme
import kotlinx.coroutines.launch

/*
    https://share.google/aimode/PYNNNicTQlfK90qsC
    Como adicionar um menu no ícone do app android com compose para abrir uma tela de setting
 */
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShortcutMenuTheme {
                val ctx = LocalContext.current
                val settingRepository = remember {
                    AppInitializer.getInstance(ctx)
                        .initializeComponent(SettingRepositoryInitializer::class.java)
                }

                val isEnabled by settingRepository.configEnabled.collectAsStateWithLifecycle(
                    initialValue = false
                )

                /*
                    Essa implementação possui um problema que vamos explorar:
                    Se o app estiver aberto em segundo plano
                 */
                val navController = rememberNavController()
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(intent) {
                    if (intent?.action == Intent.ACTION_VIEW) {
                        navController.navigate("settings")
                    }
                }


                val navToSettings = { navController.navigate("settings") }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(isEnabled, navToSettings = navToSettings)
                    }

                    composable("settings") {
                        SettingsScreen(isEnabled) { state ->
                            coroutineScope.launch {
                                settingRepository.saveConfig(state)
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(isEnabled: Boolean, navToSettings: () -> Unit) {
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
fun SettingsScreen(isEnabled: Boolean, onChangeValue: (Boolean) -> Unit) {
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
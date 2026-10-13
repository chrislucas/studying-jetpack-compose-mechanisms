package com.br.shortcutmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.startup.AppInitializer
import com.br.shortcutmenu.core.SettingRepositoryInitializer
import com.br.shortcutmenu.ui.theme.ShortcutMenuTheme
import kotlinx.coroutines.launch

/**
 * @see SettingsActivity
 *
 * Na implementacao de navegacao de
Como fazer essa navegação usando navigation component?
https://share.google/aimode/vnMkWqW4S8VgQYBjk
 * A implementacao em SettingsActivity possui um problema: Se o aplicativo ja estiver aberto
 * em segundo plano e clicamos no atalho do icone, o android pode nao disparar novamente
 * o onCreate, e a navegacao pode nao acontecer se apenas monitorarmos a intent inicial
 *
 * Para resolver isso, precisamos tratar do ciclo de vida do intent no método onNewIntent

Como testar o exemplo:

1. Instale o aplicativo no seu dispositivo ou emulador.
2. Abra o app normalmente e feche-o em seguida.
3. Na tela inicial do celular, pressione e segure o ícone do seu app.
4. O menu com o botão Configurações vai aparecer. Clique nele.
5. O app abrirá pulando direto para a tela de configurações. Altere o Switch do menu.
6. Volte para a Home e note que o estado da configuração foi salvo e atualizado imediatamente!

 */
class FixingSettingsNavigationActivity : ComponentActivity() {

    private var navController: NavController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FixingSettingsNavigationActivity", "onCreate: $intent")
        enableEdgeToEdge()
        setContent {
            ShortcutMenuTheme {
                val context = LocalContext.current
                val currentNavController = rememberNavController()
                navController = currentNavController

                val settingManager = remember {
                    AppInitializer.getInstance(context)
                        .initializeComponent(SettingRepositoryInitializer::class.java)
                }
                val isEnabled = settingManager.configEnabled.collectAsStateWithLifecycle(
                    initialValue = false
                )

                val coroutineScope = rememberCoroutineScope()

                NavHost(navController = currentNavController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            isEnabled = isEnabled.value,
                            navToSettings = { currentNavController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            isEnabled = isEnabled.value,
                            onChangeValue = { state ->
                                coroutineScope.launch {
                                    settingManager.saveConfig(state)
                                }
                            }
                        )
                    }
                }

                // Trata a intent inicial (abertura via atalho) como um EFEITO COLATERAL
                // controlado. É executado APÓS o NavHost acima já ter sido composto e
                // registrado o grafo (destinos "home"/"settings"), evitando navegar em
                // um NavController sem grafo. Além disso, LaunchedEffect(intent) só
                // reexecuta quando o `intent` muda — ao contrário de chamar
                // handlerShortcutIntent direto no corpo do composable, que rodava a cada
                // recomposição e perdia/duplicava a navegação (a tela abria e voltava).
                LaunchedEffect(intent) {
                    intent?.let(::handlerShortcutIntent)
                }
            }
        }
    }

    /*
        Chamado se o app ja estiver aberto em segundo plano e o
        usuario clicou no atalho do icone novamente
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("FixingSettingsNavigationActivity", "onNewIntent: $intent")
        setIntent(intent)
        handlerShortcutIntent(intent)
    }

    /*
        funcao que processa a intent do atalho e o navigation  componente para
        redirecionar

        1. onNewIntent(intent): Garante que o atalho funcione mesmo se o aplicativo já estiver
        aberto na barra de tarefas.
        Sem isso, o clique no atalho apenas traria o app para o primeiro plano na
        tela em que ele parou.

        2. launchSingleTop = true: esta propriedade do Navigation Component evita que múltiplas
        instâncias da tela de settings sejam abertas caso o usuário clique no atalho
        repetidas vezes seguidas.

        3. popUpTo("home"): Limpa a pilha de telas de forma inteligente. Se o usuário estiver
        profundamente navegado em outras abas do aplicativo e clicar no atalho do ícone,
        o app foca em abrir as configurações a partir da raiz (home),
        mantendo o fluxo de voltar (Back button) previsível para o usuário.
     */
    private fun handlerShortcutIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            /*
                Evita duplicar a tela de configurações se o usuário clicar várias vezes/
             */
            // Navega para o destino "settings". O bloco de opções a seguir ajusta o
            // comportamento da back stack para que cliques repetidos no atalho não
            // empilhem várias instâncias da mesma tela.
            navController?.navigate("settings") {
                // Remove da back stack tudo que estiver acima de "home" (inclusive
                // telas "settings" já abertas), deixando "home" como base. Assim,
                // ao voltar, o usuário retorna para a Home em vez de passar por
                // várias telas de settings empilhadas.
                popUpTo("home") {
                    // Salva o estado (scroll, inputs, etc.) das telas removidas pelo
                    // popUpTo, permitindo restaurá-lo depois (ver restoreState).
                    saveState = true
                }

                // Se "settings" já estiver no topo da back stack, reutiliza a
                // instância existente em vez de criar uma nova. Impede telas
                // duplicadas quando o atalho é clicado repetidamente.
                launchSingleTop = true
                // Restaura o estado previamente salvo (por saveState) desta tela,
                // caso ela esteja sendo reaberta, mantendo a experiência do usuário.
                restoreState = true
            }
        }
    }
}

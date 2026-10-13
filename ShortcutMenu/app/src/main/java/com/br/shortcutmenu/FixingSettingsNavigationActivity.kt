package com.br.shortcutmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.br.shortcutmenu.ui.theme.ShortcutMenuTheme

/*
    Como fazer essa navegação usando navigation component ?
    https://share.google/aimode/vnMkWqW4S8VgQYBjk


    Como testar o exemplo:

    1. Instale o aplicativo no seu dispositivo ou emulador.
    2. Abra o app normalmente e feche-o em seguida.
    3. Na tela inicial do celular, pressione e segure o ícone do seu app.
    4. O menu com o botão Configurações vai aparecer. Clique nele.
    5. O app abrirá pulando direto para a tela de configurações. Altere o Switch do menu.
    6. Volte para a Home e note que o estado da configuração foi salvo e atualizado imediatamente!
 */
class FixingSettingsNavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShortcutMenuTheme {

            }
        }
    }
}

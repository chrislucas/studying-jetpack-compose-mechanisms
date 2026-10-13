package com.br.navigationthroughmultimoduleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import com.br.navigation.AppDestination
import com.br.navigation.DeepLinkFactory
import com.br.navigationthroughmultimoduleapp.ui.theme.NavigationThroughMultiModuleAppTheme

/**
 * Activity inicial do app.
 *
 * Exibe a [HomeScreen] e, nos cliques, dispara **deep links** (Intents
 * implícitos `ACTION_VIEW`) para abrir as Activities dos feature modules.
 * O Android resolve esses Intents pelos `intent-filter` declarados nos
 * manifests de `:feature:wallet` e `:feature:transfer`.
 *
 * Esse é o lado **emissor** da navegação entre Activities de módulos diferentes.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationThroughMultiModuleAppTheme {
                HomeScreen(
                    onOpenWallet = {
                        startActivity(deepLinkIntent(this, AppDestination.Wallet))
                    },
                    onOpenTransfer = {
                        // Exemplo de passar informação entre Activities via deep link:
                        // já sugerimos um valor e uma conta de destino.
                        startActivity(
                            deepLinkIntent(
                                this,
                                AppDestination.Transfer(amount = 50.0, toAccount = "ACC-2"),
                            ),
                        )
                    },
                )
            }
        }
    }

    companion object {
        /**
         * Monta o Intent de deep link para um [AppDestination] usando a
         * [DeepLinkFactory]. Exposto/`internal` para ser reusado em testes.
         */
        internal fun deepLinkIntent(context: Context, destination: AppDestination): Intent =
            Intent(Intent.ACTION_VIEW, DeepLinkFactory.urlFor(destination).toUri()).apply {
                setPackage(context.packageName) // garante resolução dentro do próprio app
            }
    }
}

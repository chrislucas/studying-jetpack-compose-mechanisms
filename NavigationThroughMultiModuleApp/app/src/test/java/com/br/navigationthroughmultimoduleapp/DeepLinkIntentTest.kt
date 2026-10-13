package com.br.navigationthroughmultimoduleapp

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.br.navigation.AppDestination
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Teste de unidade do :app verificando o lado **emissor** do deep link.
 *
 * Confirma que [MainActivity.deepLinkIntent] produz o Intent correto
 * (ACTION_VIEW + URI `app://library/...`) para cada destino. Usa Robolectric
 * apenas por precisar de um Context para montar o Intent.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DeepLinkIntentTest {

    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Test
    fun walletDestinationBuildsViewIntentWithWalletUri() {
        val intent = MainActivity.deepLinkIntent(context, AppDestination.Wallet)

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("app://library/wallet", intent.data?.toString())
        assertEquals(context.packageName, intent.`package`)
    }

    @Test
    fun transferDestinationEncodesArgumentsInUri() {
        val intent = MainActivity.deepLinkIntent(
            context,
            AppDestination.Transfer(amount = 50.0, toAccount = "ACC-2"),
        )

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals(
            "app://library/transfer?amount=50.0&toAccount=ACC-2",
            intent.data?.toString(),
        )
    }
}

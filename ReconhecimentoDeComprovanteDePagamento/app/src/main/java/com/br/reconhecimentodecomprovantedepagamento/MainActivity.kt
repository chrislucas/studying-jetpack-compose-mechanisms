package com.br.reconhecimentodecomprovantedepagamento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.br.reconhecimentodecomprovantedepagamento.presentation.ui.ReceiptScreen
import com.br.reconhecimentodecomprovantedepagamento.ui.theme.ReconhecimentoDeComprovanteDePagamentoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReconhecimentoDeComprovanteDePagamentoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ReceiptScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

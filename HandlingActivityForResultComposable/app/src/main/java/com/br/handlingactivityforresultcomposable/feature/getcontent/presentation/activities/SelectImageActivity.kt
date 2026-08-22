package com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.activities.ui.theme.HandlingActivityForResultComposableTheme
import com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.screen.SelectImageScreen

class SelectImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandlingActivityForResultComposableTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SelectImageScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .systemBarsPadding()
                    )
                }
            }
        }
    }
}
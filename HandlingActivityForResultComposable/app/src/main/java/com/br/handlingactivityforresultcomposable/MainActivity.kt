package com.br.handlingactivityforresultcomposable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.handlingactivityforresultcomposable.ui.theme.HandlingActivityForResultComposableTheme


/*
    https://share.google/aimode/8DFMDtd1CvMihf0aE
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandlingActivityForResultComposableTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        TextButton(onClick = { }) {
            Text(text = "Go to Form Address Screen")
        }

        TextButton(onClick = { }) {
            Text(text = "Open BottomSheet Form Address")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HandlingActivityForResultComposableTheme {
        MainScreen()
    }
}
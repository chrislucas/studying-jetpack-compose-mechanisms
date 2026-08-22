package com.br.remoteconfig

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.br.remoteconfig.ui.theme.RemoteConfigTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RemoteConfigTheme {
                val viewModel: MainViewModel = hiltViewModel()
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val status by viewModel.status.collectAsState()
    val configValues by viewModel.configValues.collectAsState()

    MainScreenContent(
        status = status,
        configValues = configValues,
        onFetchClick = { viewModel.fetchConfig() }
    )
}

@Composable
fun MainScreenContent(
    status: FetchStatus,
    configValues: Map<String, String>,
    onFetchClick: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onFetchClick,
                enabled = status !is FetchStatus.Loading
            ) {
                Text("Fetch Config")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (status) {
                is FetchStatus.Loading -> CircularProgressIndicator()
                is FetchStatus.Success -> Text("Fetch Successful!", color = MaterialTheme.colorScheme.primary)
                is FetchStatus.Error -> Text("Error: ${status.message}", color = MaterialTheme.colorScheme.error)
                else -> {}
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Config Values:", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            configValues.forEach { (key, value) ->
                Text("$key: $value")
            }
            
            if (configValues.isEmpty() && status !is FetchStatus.Loading) {
                Text("No values fetched yet.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RemoteConfigTheme {
        MainScreenContent(
            status = FetchStatus.Success,
            configValues = mapOf(
                "api_key" to "12345",
                "feature_enabled" to "true",
                "max_items" to "10"
            ),
            onFetchClick = {}
        )
    }
}

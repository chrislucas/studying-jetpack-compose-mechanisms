package com.br.handlingactivityforresultcomposable.feature.formaddress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.usecase.IsValidFormUseCase
import com.br.handlingactivityforresultcomposable.feature.formaddress.ui.theme.HandlingActivityForResultComposableTheme
import kotlinx.serialization.json.Json

class MainScreenActivity : ComponentActivity() {
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
    val context = LocalContext.current
    var address by remember { mutableStateOf<Address?>(null) }

    val formAddressLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                address = result.data?.getStringExtra(
                    FormAddressActivity.EXTRA_ADDRESS
                )?.let { addressJson ->
                    Json.decodeFromString<Address>(addressJson)
                }
            }
        }
    )

    MainScreenContent(
        modifier = modifier,
        address = address,
        onAddressChange = { address = it },
        onGoToForm = {
            formAddressLauncher.launch(
                Intent(
                    context, FormAddressActivity::class.java
                )
            )
        }
    )
}

@Composable
fun MainScreenContent(
    modifier: Modifier = Modifier,
    address: Address? = null,
    onAddressChange: (Address?) -> Unit = {},
    onGoToForm: () -> Unit = {}
) {
    val isValidForm = remember { IsValidFormUseCase() }

    Column(modifier = modifier) {
        address.takeIf(isValidForm::invoke)?.run {
            Row {
                OutlinedTextField(
                    value = street,
                    onValueChange = { onAddressChange(address?.copy(street = it)) },
                    label = { Text("Street") },
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(2.dp)
                )

                OutlinedTextField(
                    value = number.toString(),
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            onAddressChange(address?.copy(number = newValue.toIntOrNull() ?: 0))
                        }
                    },
                    label = { Text("Number") },
                    modifier = Modifier
                        .weight(0.5f)
                        .padding(2.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        TextButton(onClick = onGoToForm) {
            Text(text = "Go to Form Address Screen")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    HandlingActivityForResultComposableTheme {
        MainScreen()
    }
}
package com.br.handlingactivityforresultcomposable.feature.formaddress

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.handlingactivityforresultcomposable.R
import com.br.handlingactivityforresultcomposable.feature.formaddress.ui.theme.HandlingActivityForResultComposableTheme
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.text.input.KeyboardType
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.usecase.IsValidFormUseCase
import kotlinx.serialization.json.Json

class FormAddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HandlingActivityForResultComposableTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormAddressScreen(
                        modifier = Modifier.padding(innerPadding)
                    ) { address ->
                        val addressJson = Json.encodeToString(Address.serializer(), address)
                        val intent = Intent().apply {
                            putExtra(EXTRA_ADDRESS, addressJson)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_ADDRESS = "extra_address"
    }
}

@Composable
fun FormAddressScreen(
    modifier: Modifier = Modifier,
    onValidAddress: (Address) -> Unit
) {

    val isValidForm = remember { IsValidFormUseCase() }
    var streetName by remember { mutableStateOf("") }
    var streetNumber by remember { mutableIntStateOf(0) }

    val resources = LocalResources.current

    Column(modifier = modifier.padding(16.dp)) {
        Row {
            OutlinedTextField(
                value = streetName,
                onValueChange = { streetName = it },
                label = { Text("Street") },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(2.dp)
            )

            OutlinedTextField(
                value = streetNumber.toString(),
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        // Only update the value if it's a valid integer
                        streetNumber = newValue.toIntOrNull() ?: 0
                        // Update the streetNumber state here if needed
                    }
                },
                label = { Text("Number") },
                modifier = Modifier
                    .weight(0.5f)
                    .padding(2.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            onClick = {
                val address = Address(streetName, streetNumber)
                if (isValidForm(address)) {
                    onValidAddress(address)
                } else {
                    // Handle invalid form submission (e.g., show an error message)
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            shape = RectangleShape
        ) {
            Text(text = resources.getString(R.string.send))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormAddressScreenPreview() {
    HandlingActivityForResultComposableTheme {
        FormAddressScreen {}
    }
}
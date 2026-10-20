package com.br.composablesstatelessandstateful.ui.stateful

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.composablesstatelessandstateful.ui.SubmitButton
import com.br.composablesstatelessandstateful.utils.isValidEmail

data class PasswordTextFieldState(
    val textFieldState: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false,
    val isError: Boolean = false,
)

/**
 * TODO criar testes de UI para as funcoes abaixo
 */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StateBasedStatefulLoginScreen(modifier: Modifier = Modifier) {
    val emailState = rememberTextFieldState()
    val isValidEmail = isValidEmail(emailState.text)
    // Só sinaliza erro quando o campo já tem conteúdo e continua inválido,
    // evitando que a tela abra com os campos em vermelho.
    val showEmailError = emailState.text.isNotEmpty() && !isValidEmail

    val passwordState = rememberTextFieldState()
    val isValidPassword = passwordState.text.length >= 6
    val showPasswordError = passwordState.text.isNotEmpty() && !isValidPassword
    var isPasswordVisible by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Acesse sua conta", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        StateBasedCustomTextField(
            valueState = emailState,
            label = "Email",
            isError = showEmailError
        )
        Spacer(modifier = Modifier.height(16.dp))
        StateBasedPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            passwordTextFieldState = PasswordTextFieldState(
                textFieldState = passwordState,
                isPasswordVisible = isPasswordVisible,
                isError = showPasswordError
            ),
            onTogglePasswordVisibility = { isVisible ->
                isPasswordVisible = !isVisible
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SubmitButton(isEnabled = isValidEmail && isValidPassword) {
            Toast.makeText(
                ctx,
                "Login realizado com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Composable
private fun StateBasedPasswordTextField(
    modifier: Modifier = Modifier,
    passwordTextFieldState: PasswordTextFieldState = PasswordTextFieldState(),
    onTogglePasswordVisibility: (Boolean) -> Unit
) {

    // INTERACTION SOURCE: Instanciamos e lembramos a fonte de interações do componente.
    // Ela captura eventos de toque, foco e arrasto (ex: se o utilizador clicou no campo ou mudou o cursor).
    // É obrigatório passar a MESMA instância tanto para a DecorationBox quanto para o Container,
    // garantindo que as animações de borda (como mudar de cor ao focar) reajam sincronizadamente.
    val interactionSource = remember { MutableInteractionSource() }

    BasicSecureTextField(
        state = passwordTextFieldState.textFieldState,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        interactionSource = interactionSource,
        textObfuscationMode = if (passwordTextFieldState.isPasswordVisible) {
            TextObfuscationMode.Visible
        } else {
            TextObfuscationMode.RevealLastTyped
        },
        // O 'decorator' permite aplicar o visual Outlined do Material 3
        decorator = @Composable {
            OutlinedTextFieldDefaults.DecorationBox(
                value = passwordTextFieldState.textFieldState.text.toString(),
                innerTextField = it,
                enabled = true,
                singleLine = true,
                isError = passwordTextFieldState.isError,
                label = { Text("Senha") },
                interactionSource = interactionSource,
                /*
                    Por que visualTransformation = VisualTransformation.None ?
                 */
                //  Vincula o cálculo visual de layout ao estado real de visibilidade.
                visualTransformation = if (passwordTextFieldState.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    val icon = if (passwordTextFieldState.isPasswordVisible) {
                        Icons.Filled.Visibility
                    } else {
                        Icons.Filled.VisibilityOff
                    }

                    val description = if (passwordTextFieldState.isPasswordVisible) {
                        "Ocultar senha"
                    } else {
                        "Mostrar senha"
                    }

                    IconButton(onClick = { onTogglePasswordVisibility(passwordTextFieldState.isPasswordVisible) }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
                // CONTAINER: É a camada responsável por desenhar o plano de fundo e o contorno (borda).
                // Substituímos o antigo 'ContainerBox' obsoleto pelo novo 'OutlinedTextFieldDefaults.Container'.
                // Ele herda as cores, formas (shapes) e espessuras padrões do Material 3 Outlined.
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = true,
                        isError = false,
                        interactionSource = interactionSource,
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                }
            )
        }
    )
}

@Composable
private fun StateBasedCustomTextField(
    valueState: TextFieldState,
    label: String,
    isError: Boolean
) {
    OutlinedTextField(
        state = valueState,
        label = { Text(label) },
        isError = isError
    )
}
package com.br.composablesstatelessandstateful.ui.stateful

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

/*
    OldLoginScreen é stateful por ser o componente raiz da estrutura. Seus
    componentes filhos são stateless e reportam a LoginScreen seu estado através de callbacks.

    Esse é um caso legítimo de uso de stateful e stateless. O Formulário representa
    um componente que vai ser usado num contexto específico e não sendo reutilizado,
    pode ser menos flexível e gerenciar seu proprio estado. Já os seus componentes
    filhos podem ser reutilizados em outros lugares, então flexibilizamos a sua implementação
    delegando o gerenciamento do seu estado e comunicação de mudança, àquele que o utiliza.

    - Vantagens dessa estrutura.
         - Reutilizacao: ValueBasedCustomTextField e ValueBasedPasswordTextField podem
         ser reutilizados

         - Separacao de conceitos: Se precisar mudar o design do botao, mexemos somente no
         componente reutilizável. Se precisar mudar a regra de validacao, podemos tocar
         no ValueBasedStatefulLoginScreen


 */

/**
 * TODO criar testes de UI para as funcoes abaixo
 */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ValueBasedStatefulLoginScreen(modifier: Modifier = Modifier) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Controla se o usuário já interagiu com cada campo, para não exibir
    // o estado de erro logo na abertura da tela (campos ainda vazios).
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val isValidEmail = isValidEmail(email)
    val isValidPassword = password.length >= 6

    // Só sinaliza erro depois que o campo foi tocado e continua inválido.
    val showEmailError = emailTouched && !isValidEmail
    val showPasswordError = passwordTouched && !isValidPassword

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
        ValueBasedCustomTextField(
            value = email,
            label = "Email",
            isError = showEmailError,
            onValueChange = {
                email = it
                emailTouched = true
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        ValueBasedPasswordTextField(
            value = password,
            isError = showPasswordError,
            onValueChange = {
                password = it
                passwordTouched = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        SubmitButton(isEnabled = isValidEmail && isValidPassword, onClick = {
            Toast.makeText(
                ctx, "Login realizado com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}

@Composable
private fun ValueBasedCustomTextField(
    value: String,
    label: String,
    isError: Boolean,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        singleLine = true,
    )
}

@Composable
private fun ValueBasedPasswordTextField(
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password") },
        singleLine = true,
        // Alterna dinamicamente a transformação visual do texto
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        // Configura o teclado virtual para o modo de senha
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        // Ícone clicável à direita para alternar a visibilidade
        trailingIcon = {
            val icon = if (isPasswordVisible) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            val description = if (isPasswordVisible) {
                "Ocultar senha"
            } else {
                "Mostrar senha"
            }

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = icon, contentDescription = description)
            }
        },
        isError = isError
    )
}


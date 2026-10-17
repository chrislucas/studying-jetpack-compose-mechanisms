package com.br.composablesstatelessandstateful.ui.stateless

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel


data class LoginUiState(
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val isPasswordVisible: Boolean = false
)

class LoginViewModel: ViewModel() {

}

/**
 * TODO
 * Criar uma função Stateless baseada em
 * @see com.br.composablesstatelessandstateful.ui.stateful.StateBasedStatefulLoginScreen
 *
 * Criar testes de UI para as funcoes abaixo.
 *
 */

@Composable
fun StateBaseStatelessLoginScreen() {

}
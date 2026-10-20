package com.br.composablesstatelessandstateful.ui.stateful

/**
 * Test tags usados pela [ValueBasedStatefulLoginScreen] e seus componentes.
 *
 * Centralizar as tags evita strings mágicas duplicadas entre o código de
 * produção e os testes (unit Robolectric e instrumentado).
 */
object ValueBasedLoginTestTags {
    const val EMAIL_FIELD = "login_email_field"
    const val PASSWORD_FIELD = "login_password_field"
    const val SUBMIT_BUTTON = "login_submit_button"
    const val TOGGLE_PASSWORD_VISIBILITY = "login_toggle_password_visibility"
}

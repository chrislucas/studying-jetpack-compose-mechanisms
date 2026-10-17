package com.br.composablesstatelessandstateful.utils

/**
 * Regex simples para validação de email no formato `local@dominio.tld`.
 *
 * - Parte local: letras, dígitos e os caracteres `. _ % + -`
 * - Domínio: letras, dígitos, `.` e `-`
 * - TLD: pelo menos 2 letras
 */
private val EMAIL_REGEX = Regex(
    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
)

/**
 * Valida se a [email] informado é um endereço de email bem formado.
 *
 * A string é normalizada (trim) antes da validação. Valores em branco
 * retornam `false`.
 *
 * @param email o endereço de email a ser validado.
 * @return `true` se o email for válido, `false` caso contrário.
 */
fun isValidEmail(email: CharSequence): Boolean {
    val normalized = email.trim()
    if (normalized.isBlank()) return false
    return EMAIL_REGEX.matches(normalized)
}

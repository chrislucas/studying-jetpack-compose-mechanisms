package com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val street: String,
    val number: Int,
)

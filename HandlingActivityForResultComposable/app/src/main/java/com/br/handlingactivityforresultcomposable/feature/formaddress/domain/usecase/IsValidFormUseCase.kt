package com.br.handlingactivityforresultcomposable.feature.formaddress.domain.usecase

import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address

class IsValidFormUseCase {
    operator fun invoke(address: Address?): Boolean {
        return address?.run { street.isNotEmpty() && number > 0 } ?: false
    }
}

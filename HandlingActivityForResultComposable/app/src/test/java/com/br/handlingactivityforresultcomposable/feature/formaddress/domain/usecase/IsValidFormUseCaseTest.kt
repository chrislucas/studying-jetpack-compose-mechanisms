package com.br.handlingactivityforresultcomposable.feature.formaddress.domain.usecase

import com.br.handlingactivityforresultcomposable.feature.formaddress.domain.model.Address
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IsValidFormUseCaseTest {

    private lateinit var useCase: IsValidFormUseCase

    @Before
    fun setUp() {
        useCase = IsValidFormUseCase()
    }

    @Test
    fun `invoke should return false when address is null`() {
        val result = useCase(null)
        assertFalse(result)
    }

    @Test
    fun `invoke should return false when street is empty`() {
        val address = Address(street = "", number = 123)
        val result = useCase(address)
        assertFalse(result)
    }

    @Test
    fun `invoke should return false when number is zero`() {
        val address = Address(street = "Main St", number = 0)
        val result = useCase(address)
        assertFalse(result)
    }

    @Test
    fun `invoke should return false when number is negative`() {
        val address = Address(street = "Main St", number = -1)
        val result = useCase(address)
        assertFalse(result)
    }

    @Test
    fun `invoke should return true when address is valid`() {
        val address = Address(street = "Main St", number = 123)
        val result = useCase(address)
        assertTrue(result)
    }
}

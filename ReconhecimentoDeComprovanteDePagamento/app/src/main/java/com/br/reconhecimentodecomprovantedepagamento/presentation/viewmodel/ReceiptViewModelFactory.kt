package com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.br.reconhecimentodecomprovantedepagamento.data.repository.ReceiptProcessorRepositoryImpl
import com.br.reconhecimentodecomprovantedepagamento.domain.usecase.ClassifyReceiptUseCase

/**
 * Factory manual para o [ReceiptViewModel].
 *
 * O projeto não usa um framework de DI, então a construção da árvore de
 * dependências (Repository → UseCase → ViewModel) é feita aqui, recebendo
 * apenas o [Context] da aplicação.
 */
class ReceiptViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptViewModel::class.java)) {
            val repository = ReceiptProcessorRepositoryImpl(context.applicationContext)
            val useCase = ClassifyReceiptUseCase(repository)
            return ReceiptViewModel(useCase) as T
        }
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}

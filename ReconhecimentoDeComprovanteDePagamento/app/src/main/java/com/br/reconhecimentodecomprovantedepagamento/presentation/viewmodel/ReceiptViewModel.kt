package com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.reconhecimentodecomprovantedepagamento.domain.usecase.ClassifyReceiptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que gerencia a pipeline OCR → Classificação.
 *
 * Recebe um [Bitmap] da UI, aciona o [ClassifyReceiptUseCase] e emite
 * [ReceiptUiState] via [StateFlow] conforme o padrão UDF descrito no design.
 */
class ReceiptViewModel(
    private val classifyReceipt: ClassifyReceiptUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReceiptUiState>(ReceiptUiState.Idle)
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    /**
     * Inicia a classificação do comprovante.
     *
     * Transições de estado:
     * 1. [ReceiptUiState.Processing]
     * 2. [ReceiptUiState.Success] ou [ReceiptUiState.Error]
     */
    fun classify(bitmap: Bitmap) {
        _uiState.value = ReceiptUiState.Processing

        viewModelScope.launch {
            classifyReceipt(bitmap)
                .onSuccess { result ->
                    _uiState.value = ReceiptUiState.Success(
                        category = result.category,
                        score = result.score,
                        rawText = result.rawText
                    )
                }
                .onFailure { error ->
                    _uiState.value = ReceiptUiState.Error(
                        message = error.message ?: "Erro desconhecido ao classificar o comprovante."
                    )
                }
        }
    }

    /** Reseta o estado para [ReceiptUiState.Idle] (ex.: ao descartar resultado). */
    fun reset() {
        _uiState.value = ReceiptUiState.Idle
    }
}

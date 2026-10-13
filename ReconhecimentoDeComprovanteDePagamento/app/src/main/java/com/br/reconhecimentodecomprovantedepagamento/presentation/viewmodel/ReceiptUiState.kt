package com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel

import com.br.reconhecimentodecomprovantedepagamento.domain.model.ReceiptCategory

/**
 * Estado da UI para a tela de reconhecimento de comprovantes.
 * Segue o padrão UDF — emitido via [StateFlow] pelo [ReceiptViewModel].
 */
sealed interface ReceiptUiState {

    /** Estado inicial: nenhum comprovante foi submetido. */
    data object Idle : ReceiptUiState

    /** OCR + classificação em andamento. */
    data object Processing : ReceiptUiState

    /** Classificação concluída com sucesso. */
    data class Success(
        val category: ReceiptCategory,
        val score: Float,
        val rawText: String
    ) : ReceiptUiState

    /** Falha na pipeline (IO, modelo ausente, OCR vazio, etc.). */
    data class Error(val message: String) : ReceiptUiState
}

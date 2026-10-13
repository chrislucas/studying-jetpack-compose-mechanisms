package com.br.reconhecimentodecomprovantedepagamento.domain.model

/**
 * Resultado bruto da inferência do classificador: a categoria de maior score e
 * o próprio score (0.0..1.0). O limiar de confiança (RF-03) é aplicado na
 * camada de domínio ([com.br.reconhecimentodecomprovantedepagamento.domain.usecase.ClassifyReceiptUseCase]),
 * não aqui, mantendo o repositório livre de regra de negócio.
 */
data class ClassificationResult(
    val category: ReceiptCategory,
    val score: Float,
    val rawText: String
)

package com.br.reconhecimentodecomprovantedepagamento.domain.usecase

import android.graphics.Bitmap
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ClassificationResult
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ReceiptCategory
import com.br.reconhecimentodecomprovantedepagamento.domain.repository.ReceiptProcessorRepository

/**
 * Orquestra a pipeline completa OCR → Classificação → Validação de confiança.
 *
 * O limiar mínimo de confiança (RF-03 / Critérios de Aceitação) é aplicado
 * aqui na camada de domínio, mantendo o repositório livre de regra de negócio.
 */
class ClassifyReceiptUseCase(
    private val repository: ReceiptProcessorRepository
) {
    /**
     * Score mínimo para que a classificação seja considerada válida.
     * Valores abaixo retornam [ReceiptCategory.UNKNOWN].
     */
    companion object {
        const val MIN_CONFIDENCE = 0.65f
    }

    /**
     * Executa a pipeline de classificação completa a partir de um [Bitmap].
     *
     * @return [ClassificationResult] com a categoria final — se o score
     * ficar abaixo de [MIN_CONFIDENCE], a categoria é [ReceiptCategory.UNKNOWN].
     */
    suspend operator fun invoke(bitmap: Bitmap): Result<ClassificationResult> {
        val rawText = repository.extractText(bitmap).getOrElse { return Result.failure(it) }
        return classifyFromText(rawText)
    }

    /**
     * Classifica o texto já extraído e aplica o limiar de confiança (RF-03).
     *
     * Esta função concentra toda a regra de negócio e é livre de dependências
     * do framework Android, permitindo testes unitários puros na JVM.
     */
    suspend fun classifyFromText(rawText: String): Result<ClassificationResult> {
        if (rawText.isBlank()) {
            return Result.failure(
                IllegalStateException("Nenhum texto foi reconhecido no comprovante.")
            )
        }

        val result = repository.classifyText(rawText).getOrElse { return Result.failure(it) }

        // Aplica o threshold de confiança (RF-03)
        return if (result.score >= MIN_CONFIDENCE) {
            Result.success(result)
        } else {
            Result.success(result.copy(category = ReceiptCategory.UNKNOWN))
        }
    }
}

package com.br.reconhecimentodecomprovantedepagamento.domain.repository

import android.graphics.Bitmap
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ClassificationResult

/**
 * Contrato para a pipeline OCR → Classificação.
 *
 * A interface é definida na camada de domínio para desacoplar a lógica de
 * negócio das dependências concretas de ML Kit e TFLite Task Library.
 */
interface ReceiptProcessorRepository {

    /** Extrai texto legível a partir de um [Bitmap] de comprovante (RF-01). */
    suspend fun extractText(bitmap: Bitmap): Result<String>

    /**
     * Classifica o [text] extraído e retorna o [ClassificationResult] com a
     * categoria de maior pontuação (RF-02). Não aplica limiar de confiança;
     * isso é responsabilidade da camada de domínio (UseCase).
     */
    suspend fun classifyText(text: String): Result<ClassificationResult>
}

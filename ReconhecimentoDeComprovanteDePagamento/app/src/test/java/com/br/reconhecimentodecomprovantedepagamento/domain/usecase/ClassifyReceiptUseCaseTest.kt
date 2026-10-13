package com.br.reconhecimentodecomprovantedepagamento.domain.usecase

import android.graphics.Bitmap
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ClassificationResult
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ReceiptCategory
import com.br.reconhecimentodecomprovantedepagamento.domain.repository.ReceiptProcessorRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Testes unitários (JVM puro) para [ClassifyReceiptUseCase].
 *
 * Exercitam [ClassifyReceiptUseCase.classifyFromText], que concentra toda a
 * regra de negócio (limiar de confiança RF-03 e propagação de falhas) sem
 * depender de tipos do framework Android — portanto não exigem Robolectric.
 *
 * O repositório é substituído por um Fake configurável (não um mock). O método
 * [ReceiptProcessorRepository.extractText] é exigido pela interface mas nunca é
 * chamado nestes testes, por isso lança caso seja invocado por engano.
 */
class ClassifyReceiptUseCaseTest {

    private class FakeReceiptProcessorRepository(
        private val classifyResult: Result<ClassificationResult>
    ) : ReceiptProcessorRepository {
        override suspend fun extractText(bitmap: Bitmap): Result<String> =
            error("extractText não deveria ser chamado neste teste")

        override suspend fun classifyText(text: String): Result<ClassificationResult> = classifyResult
    }

    @Test
    fun `score acima do limiar retorna categoria original`() = runTest {
        val repo = FakeReceiptProcessorRepository(
            Result.success(ClassificationResult(ReceiptCategory.PIX, 0.90f, "Comprovante de PIX recebido"))
        )
        val useCase = ClassifyReceiptUseCase(repo)

        val result = useCase.classifyFromText("Comprovante de PIX recebido")

        assertTrue(result.isSuccess)
        assertEquals(ReceiptCategory.PIX, result.getOrThrow().category)
    }

    @Test
    fun `score abaixo do limiar retorna UNKNOWN`() = runTest {
        val repo = FakeReceiptProcessorRepository(
            Result.success(ClassificationResult(ReceiptCategory.BOLETO, 0.50f, "texto ambíguo"))
        )
        val useCase = ClassifyReceiptUseCase(repo)

        val result = useCase.classifyFromText("texto ambíguo")

        assertTrue(result.isSuccess)
        assertEquals(ReceiptCategory.UNKNOWN, result.getOrThrow().category)
    }

    @Test
    fun `score exatamente no limiar e considerado valido`() = runTest {
        val repo = FakeReceiptProcessorRepository(
            Result.success(
                ClassificationResult(ReceiptCategory.BOLETO, ClassifyReceiptUseCase.MIN_CONFIDENCE, "Linha digitável")
            )
        )
        val useCase = ClassifyReceiptUseCase(repo)

        val result = useCase.classifyFromText("Linha digitável")

        assertEquals(ReceiptCategory.BOLETO, result.getOrThrow().category)
    }

    @Test
    fun `texto vazio retorna failure sem chamar o classificador`() = runTest {
        val repo = FakeReceiptProcessorRepository(
            Result.success(ClassificationResult(ReceiptCategory.PIX, 0.99f, ""))
        )
        val useCase = ClassifyReceiptUseCase(repo)

        val result = useCase.classifyFromText("   ")

        assertTrue(result.isFailure)
    }

    @Test
    fun `falha na classificacao propaga failure`() = runTest {
        val repo = FakeReceiptProcessorRepository(
            Result.failure(RuntimeException("Modelo ausente"))
        )
        val useCase = ClassifyReceiptUseCase(repo)

        val result = useCase.classifyFromText("Comprovante")

        assertTrue(result.isFailure)
    }
}

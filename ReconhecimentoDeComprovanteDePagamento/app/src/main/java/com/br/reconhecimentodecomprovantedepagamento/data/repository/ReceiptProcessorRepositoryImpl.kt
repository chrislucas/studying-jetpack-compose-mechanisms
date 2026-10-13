package com.br.reconhecimentodecomprovantedepagamento.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ClassificationResult
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ReceiptCategory
import com.br.reconhecimentodecomprovantedepagamento.domain.repository.ReceiptProcessorRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implementação on-device da pipeline OCR → Classificação.
 *
 * - **OCR:** Google ML Kit Text Recognition (Latin), executado localmente.
 * - **Classificação:** TensorFlow Lite Task Library [NLClassifier], carregado
 *   a partir de [MODEL_ASSET_PATH] nos assets do app.
 *
 * O [NLClassifier] é inicializado de forma preguiçosa e thread-safe na primeira
 * chamada de classificação, capturando falhas de IO (modelo ausente/corrompido)
 * e propagando-as como [Result.failure].
 */
class ReceiptProcessorRepositoryImpl(
    private val context: Context
) : ReceiptProcessorRepository {

    companion object {
        /** Caminho do modelo dentro de `app/src/main/assets/`. */
        const val MODEL_ASSET_PATH = "comprovantes_model.tflite"
    }

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    /**
     * Classificador carregado de forma preguiçosa e thread-safe.
     * A inicialização pode falhar se o modelo estiver ausente — isso é
     * capturado pelo [runCatching] em [classifyText].
     */
    @Volatile
    private var classifier: NLClassifier? = null

    private fun getOrCreateClassifier(): NLClassifier {
        return classifier ?: synchronized(this) {
            classifier ?: NLClassifier.createFromFile(context, MODEL_ASSET_PATH)
                .also { classifier = it }
        }
    }

    // --- OCR (RF-01) ---

    override suspend fun extractText(bitmap: Bitmap): Result<String> =
        withContext(Dispatchers.Default) {
            runCatching {
                val image = InputImage.fromBitmap(bitmap, 0)
                awaitRecognition(image)
            }
        }

    /** Converte o callback Task do ML Kit em suspending function. */
    private suspend fun awaitRecognition(image: InputImage): String =
        suspendCancellableCoroutine { cont ->
            recognizer.process(image)
                .addOnSuccessListener { visionText -> cont.resume(visionText.text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    // --- Classificação (RF-02) ---

    override suspend fun classifyText(text: String): Result<ClassificationResult> =
        withContext(Dispatchers.Default) {
            runCatching {
                val nlClassifier = getOrCreateClassifier()
                val categories = nlClassifier.classify(text)

                val best = categories.maxByOrNull { it.score }
                    ?: throw IllegalStateException("O classificador não retornou categorias.")

                ClassificationResult(
                    category = ReceiptCategory.fromLabel(best.label),
                    score = best.score,
                    rawText = text
                )
            }
        }
}

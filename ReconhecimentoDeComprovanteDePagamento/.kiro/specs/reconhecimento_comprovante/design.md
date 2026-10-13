# Design: Arquitetura do Classificador TFLite + OCR

## 1. Decisões Tecnológicas & Dependências
*   **OCR Engine:** Google ML Kit Text Recognition (Latin). Escolhido por rodar nativamente on-device via TensorFlow Lite integrado e possuir excelente suporte a Kotlin Coroutines (`play-services-mlkit-text-recognition`).
*   **NLP Classifier:** TensorFlow Lite Task Library Text (`tensorflow-lite-task-text`). Fornece uma API de alto nível (`NLClassifier`) otimizada para inferência de texto local, eliminando a necessidade de tokenização manual complexa em Kotlin.

## 2. Contratos de Dados & Interfaces

### UI State Pattern
```kotlin
sealed interface ReceiptUiState {
    object Idle : ReceiptUiState
    object Processing : ReceiptUiState
    data class Success(val category: String, val rawText: String) : ReceiptUiState
    data class Error(val message: String) : ReceiptUiState
}
```

### Contrato do Repositório
```kotlin
interface ReceiptProcessorRepository {
    suspend fun extractText(bitmap: Bitmap): Result<String>
    suspend fun classifyText(text: String): Result<String>
}
```

## 3. Fluxo de Dados (UDF)
1. A View captura ou recebe o `Bitmap` do comprovante e dispara o evento no `ViewModel`.
2. O `ViewModel` altera o estado para `Processing` e aciona o `UseCase`.
3. O `Repository` envia o bitmap para o `ML Kit`, recebe o texto bruto e o repassa imediatamente para o `NLClassifier` do TFLite.
4. O resultado com maior `score` (acima de 0.65) é envelopado em `ReceiptUiState.Success` e emitido via `StateFlow` de volta para a UI.

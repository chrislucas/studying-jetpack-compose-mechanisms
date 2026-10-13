# Modelo TFLite de Classificação de Comprovantes

Coloque o arquivo do modelo treinado neste diretório com o nome exato:

```
comprovantes_model.tflite
```

## Requisitos do modelo (ver .kiro/specs/reconhecimento_comprovante)

- Formato compatível com a TensorFlow Lite Task Library `NLClassifier`.
- Entrada: texto bruto (string) extraído via OCR.
- Saída: distribuição de probabilidade sobre as labels `PIX`, `BOLETO`, `CARTAO`.
- Tamanho máximo: 5 MB (RNF-03).
- Tempo de inferência alvo: < 200 ms em hardware intermediário (RNF-02).

## Observação de build

O `app/build.gradle.kts` declara `androidResources { noCompress += "tflite" }`
para impedir a compactação do modelo no APK, permitindo o memory-mapping direto
pelo interpretador TFLite.

Enquanto o arquivo `comprovantes_model.tflite` não estiver presente, o
`ReceiptProcessorRepositoryImpl` tratará a ausência do modelo como uma falha de
inicialização e emitirá `ReceiptUiState.Error`.

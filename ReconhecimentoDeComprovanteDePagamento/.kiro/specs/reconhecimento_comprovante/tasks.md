# Tasks: Implementação do Classificador de Comprovantes

## Configuração Base
- [ ] Criar o diretório `app/src/main/assets/` caso não exista e colar o arquivo `comprovantes_model.tflite`.
- [ ] Adicionar as dependências de OCR e TFLite Task Text no `app/build.gradle.kts`.
- [ ] Adicionar a configuração `aaptOptions { noCompress "tflite" }` no Gradle para evitar que o modelo seja compactado incorretamente no build do APK.

## Camada Data & Infraestrutura
- [ ] Criar a interface `ReceiptProcessorRepository` no pacote `domain/repository/`.
- [ ] Implementar `ReceiptProcessorRepositoryImpl` no pacote `data/repository/`.
    - [ ] Implementar extração de texto via `TextRecognition.getClient()`.
    - [ ] Implementar inicialização segura do `NLClassifier` capturando exceções de IO.
    - [ ] Criar lógica de threshold (filtro de `score > 0.65`).

## Camada de Domínio & Negócio
- [ ] Criar a classe `ClassifyReceiptUseCase` injetando o repositório.
- [ ] Criar testes unitários para o `UseCase` injetando uma implementação estável de teste (Fake).

## Camada de Apresentação (UI)
- [ ] Criar a Sealed Interface `ReceiptUiState` em `presentation/viewmodel/`.
- [ ] Implementar `ReceiptViewModel` gerenciando o escopo com `viewModelScope.launch` e tratamento de erros.
- [ ] Construir a tela ou componente em Jetpack Compose que reage aos estados do `ReceiptUiState`.

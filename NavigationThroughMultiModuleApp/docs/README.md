# Documentação — NavigationThroughMultiModuleApp

Projeto didático de um **app de finanças ("Meu Banco")** que demonstra navegação
com **Jetpack Navigation 3** em um cenário **multi-módulo**, incluindo:

- navegação entre **Activities de módulos diferentes via deep link**;
- navegação entre **composables via Navigation 3** (back stack);
- passagem de informação **entre Activities** (deep link) e **entre composables**
  (argumentos de rota);
- **DI manual** (sem framework) através de um `Navigator`;
- composables **acessíveis** via API de `semantics`;
- **testes** de unidade (JUnit + MockK) e de UI na JVM (Robolectric + Compose Test).

## Índice

| Documento | Conteúdo |
|-----------|----------|
| [01-arquitetura.md](01-arquitetura.md) | Visão geral da arquitetura, módulos, camadas e diagramas de fluxo. |
| [02-componentes.md](02-componentes.md) | Cada componente implementado, sua localização e responsabilidade por camada. |
| [03-padroes.md](03-padroes.md) | Padrões de projeto e convenções utilizados. |
| [04-funcionalidades.md](04-funcionalidades.md) | Lista de funcionalidades do app. |
| [05-cobertura-de-testes.md](05-cobertura-de-testes.md) | O que está testado e, principalmente, **o que falta testar** (para treino). |

## Como rodar

```bash
# Build de todos os módulos
./gradlew assembleDebug

# Todos os testes de unidade/UI na JVM
./gradlew testDebugUnitTest

# Testar um módulo específico
./gradlew :navigation:testDebugUnitTest
```

> **Nota de ambiente:** o daemon do Gradle roda em JDK 25, mas o Robolectric
> 4.14.1 só instrumenta bytecode até o JDK 21. Por isso as tasks de teste dos
> módulos `:app`, `:feature:wallet` e `:feature:transfer` são fixadas em um
> `javaLauncher` **Java 17** (ver bloco `tasks.withType<Test>` nos respectivos
> `build.gradle.kts`). O daemon não é alterado.

## Deep links disponíveis

| URL | Abre | Argumentos |
|-----|------|------------|
| `app://library/wallet` | Carteira | — |
| `app://library/transfer` | Transferência | `amount` (Double), `toAccount` (String) — opcionais |

Exemplo de disparo manual via adb:

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "app://library/transfer?amount=50.0&toAccount=ACC-2"
```

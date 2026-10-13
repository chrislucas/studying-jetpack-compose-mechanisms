# 03 — Padrões e convenções

## Padrões de arquitetura

### Modularização por feature
Cada funcionalidade vive em seu próprio módulo (`:feature:wallet`,
`:feature:transfer`), sem dependência entre features. Benefícios: builds
incrementais, limites claros de responsabilidade e reuso do `:navigation`.

### Módulo de contrato compartilhado
O `:navigation` concentra o "contrato comum" de navegação (`AppDestination`,
parser/factory, `Navigator`). Features e `:app` dependem desse contrato, não uns
dos outros. Isso evita acoplamento direto entre quem **emite** e quem **recebe**
um deep link.

### Injeção de dependência manual (sem framework)
Não há Hilt/Koin. O `Navigator` é criado explicitamente (em geral no `onCreate`
da Activity, via `rememberWalletNavigator()` / `rememberTransferNavigator(...)`)
e **passado por parâmetro** aos composables. Vantagem didática: deixa visível
"quem cria o quê e quem recebe o quê", sem mágica de container.

## Padrões de projeto (GoF e correlatos)

| Padrão | Onde | Como se manifesta |
|--------|------|-------------------|
| **Factory** | `DeepLinkFactory` | Centraliza a construção de URLs de deep link a partir de um destino. |
| **Strategy / resolução por tipo** | `entryProvider` nos `*NavHost` | Um `when (key)` resolve cada `NavKey` para o composable correspondente. |
| **Command / state as data** | `Navigator` + `NavKey`s | Navegar é apenas adicionar/remover itens de uma lista observável (back stack). |
| **Sealed hierarchy (type-safe routes)** | `AppDestination`, `WalletRoute`, `TransferRoute` | Rotas tipadas e exaustivas; o compilador garante o tratamento de todos os casos. |
| **Facade** | `DeepLinkParser` | Esconde o parsing de URL atrás de uma API simples (`parse(url): AppDestination?`). |
| **Separation of concerns (Host vs Activity)** | `*NavHost` extraído da `*Activity` | Permite testar a navegação por UI sem o ciclo de vida de Activity. |

## Convenções do Navigation 3

- **Rotas são `NavKey` + `@Serializable`** para sobreviver a mudanças de
  configuração e morte de processo.
- **Argumentos viajam na própria rota** (ex.: `AccountDetail(accountId)`,
  `Confirmation(amount, toAccount)`) — é assim que se passa dado entre composables.
- **`NavDisplay`** observa o back stack (`SnapshotStateList`) e recompõe
  automaticamente quando ele muda.
- **`replaceTop`** é usado quando não se deseja permitir voltar à tela anterior
  (ex.: após confirmar a transferência, o "voltar" não retorna à confirmação).

## Convenções de deep link

- **Scheme/host fixos:** `app://library`.
- **Path identifica o destino:** `/wallet`, `/transfer`.
- **Query carrega argumentos:** `?amount=..&toAccount=..`.
- **Round-trip garantido:** `DeepLinkParser.parse(DeepLinkFactory.urlFor(d)) == d`
  (coberto por teste).
- **Resolução dentro do app:** o `Intent` recebe `setPackage(packageName)` para
  resolver na própria aplicação.

## Convenções de acessibilidade (API `semantics`)

- `Modifier.semantics { heading() }` em todos os títulos de tela.
- `contentDescription` em botões e itens clicáveis, descrevendo a ação/conteúdo.
- `semantics(mergeDescendants = true)` para agrupar um item composto (ex.: card de
  conta) em um único nó anunciável.
- Os **testes de UI localizam elementos por `contentDescription`**, exatamente
  como um leitor de tela faria — isso reforça que a acessibilidade está correta.

## Convenções de teste

- **Unidade (lógica pura):** JUnit 4; `MockK` quando é útil fabricar
  dependências (ex.: `NavKey` no `NavigatorTest`).
- **UI na JVM:** `Robolectric` + `androidx.compose.ui.test` (`createComposeRule`),
  anotados com `@RunWith(RobolectricTestRunner::class)` e `@Config(sdk = [34])`.
- **Separação Host/Activity** permite testar o fluxo de telas sem instrumentação
  em device.

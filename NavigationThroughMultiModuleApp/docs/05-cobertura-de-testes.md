# 05 — Cobertura de testes (o que falta para você treinar)

Este documento mapeia **o que já está testado** e, principalmente, **o que ainda
não está**, com sugestões concretas de testes para você implementar.

> Estado atual (todos passando na JVM):
> `:navigation` 19 testes · `:app` 3 · `:feature:wallet` 3 · `:feature:transfer` 4.

## Legenda
- ✅ **Coberto**: existe teste direto.
- 🟡 **Parcial**: há teste, mas caminhos/estados importantes faltam.
- ❌ **Sem teste**: nenhuma cobertura.

---

## Módulo `:navigation`

| Classe | Status | Observações |
|--------|--------|-------------|
| `DeepLinkParser` | ✅ | `DeepLinkParserTest` cobre paths, query, encode, erros, nulos. |
| `DeepLinkFactory` | ✅ | `DeepLinkFactoryTest` cobre URLs e round-trip. |
| `Navigator` | ✅ | `NavigatorTest` cobre `goTo`/`goBack`/`replaceTop`/`canGoBack` (com MockK). |
| `AppDestination` | 🟡 | Exercitado indiretamente pelo parser/factory. Não há teste próprio (é só um contrato de dados). |

**Para treinar aqui (extensões):**
- Testar `DeepLinkParser` com **query malformada** adicional (ex.: `?=valor`,
  `?amount`), paths com barra final, e `toAccount` vazio.
- Testar `DeepLinkFactory` quando **apenas um** dos argumentos de `Transfer`
  está presente (só `amount`, só `toAccount`).

---

## Módulo `:app`

| Classe / Função | Status | Observações |
|-----------------|--------|-------------|
| `MainActivity.deepLinkIntent()` | ✅ | `DeepLinkIntentTest` valida action, URI e package para Wallet e Transfer. |
| `HomeScreen` | ✅ | `HomeScreenUiTest` valida acessibilidade dos botões e disparo dos callbacks. |
| `MainActivity` (ciclo de vida / `setContent`) | ❌ | O corpo do `onCreate` e a fiação dos callbacks reais (que chamam `startActivity`) não são testados. |
| `ui/theme/*` | ❌ | Tema gerado por template; normalmente não se testa. |

**Para treinar aqui:**
- Teste de UI/integração com **Robolectric + Intents** (`Shadows.shadowOf(app)
  .nextStartedActivity` ou `Intents.intended`) verificando que tocar em
  "Carteira"/"Transferência" realmente dispara o `Intent` de deep link correto a
  partir da `MainActivity` (e não só a `HomeScreen` isolada).

---

## Módulo `:feature:wallet`

| Classe | Status | Observações |
|--------|--------|-------------|
| `WalletRoute` | ✅ | Exercitado por `WalletNavigationTest` (argumento `accountId`). |
| `WalletData` | ✅ | `WalletNavigationTest` cobre `byId` para id existente. |
| `WalletNavHost` + `WalletScreens` | 🟡 | `WalletNavHostUiTest` cobre o caminho feliz Menu→Accounts→Detail. |
| `WalletActivity` | ❌ | Entrypoint / parsing de deep link / `onExit` não testados. |

**Caminhos/estados não cobertos (sugestões de teste):**
- `WalletData.byId("ID-inexistente")` deve retornar `null` (unit).
- `AccountDetailScreen` com `account = null` → deve exibir **"Conta não
  encontrada"** (UI).
- Botão **"Voltar"** do detalhe retorna à lista de contas (UI, `goBack`).
- `WalletNavHost.onExit` é chamado ao voltar na **raiz** (Menu) — ex.: via
  `navigator.goBack()` retornando `false`.
- `WalletActivity`: abrir via deep link `app://library/wallet` com Robolectric e
  checar que a UI inicial é o menu; e que um deep link inesperado dispara o
  `require(...)`.
- Acessibilidade: cada `AccountsScreen` item expõe `contentDescription`
  com nome + saldo (assert de semântica).

---

## Módulo `:feature:transfer`

| Classe | Status | Observações |
|--------|--------|-------------|
| `TransferRoute` | ✅ | Exercitado por `TransferFlowTest`. |
| `TransferNavHost` + `TransferScreens` | 🟡 | `TransferNavHostUiTest` cobre o fluxo com formulário **pré-preenchido** → confirmação → recibo. |
| `TransferActivity` | ✅ | `TransferActivityDeepLinkTest` inicia a Activity real com `Intent(ACTION_VIEW)` e valida prefill via deep link, fluxo até o recibo e o caso sem argumentos. |

**Caminhos/estados não cobertos (sugestões de teste):**
- **Validação do formulário:** com campos vazios o botão "Continuar" fica
  **desabilitado**; preencher valor inválido (`"abc"`) mantém desabilitado (UI).
- **Preenchimento manual** (sem prefill): digitar valor e conta, avançar, e
  verificar que `Confirmation` recebe os valores digitados (UI com
  `performTextInput`).
- Botão **"Voltar"** da confirmação retorna ao formulário (UI, `goBack`).
- Botão **"Concluir"** do recibo chama `onExit` (UI).

> Já implementado como exemplo-modelo: `TransferActivityDeepLinkTest` cobre a
> abertura via deep link **com** argumentos (prefill + fluxo até o recibo) e
> **sem** argumentos (formulário vazio). Use-o como referência para os demais.

---

## Resumo das classes SEM teste direto (prioridade para treino)

1. `WalletActivity` — entrypoint + deep link (❌).
2. `MainActivity` — fiação real dos callbacks que disparam `startActivity` (❌,
   a `HomeScreen` isolada já é coberta).
3. Caminhos de UI não-felizes: conta inexistente, validação do formulário,
   navegação "voltar", "concluir" (🟡).

> `TransferActivity` já saiu desta lista — virou o exemplo-modelo
> (`TransferActivityDeepLinkTest`). Replique a mesma abordagem para
> `WalletActivity`.

## Dicas práticas para implementar

- **Activities com Robolectric:** use `Robolectric.buildActivity(X::class.java,
  intent).setup()` passando um `Intent` com a `Uri` do deep link; combine com
  `createAndroidComposeRule<X>()` para assertar a UI.
- **Intents disparados:** `shadowOf(ApplicationProvider.getApplicationContext()
  as Application).nextStartedActivity` recupera o último `Intent` iniciado.
- **Entrada de texto no Compose:** `onNode(...).performTextInput("50")`.
- **Semântica:** prefira localizar por `contentDescription`/`text` para reforçar
  a acessibilidade; use `assert(hasClickAction())`, `assertIsEnabled()` /
  `assertIsNotEnabled()` para estados de botão.
- Mantenha o padrão `@RunWith(RobolectricTestRunner::class)` +
  `@Config(sdk = [34])` já usado nos testes existentes.

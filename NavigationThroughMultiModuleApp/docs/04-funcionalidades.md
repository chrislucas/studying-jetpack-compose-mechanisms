# 04 — Funcionalidades

## Visão geral

O app "Meu Banco" oferece dois recursos acessíveis a partir de um menu inicial,
cada um morando em seu próprio módulo e aberto via deep link.

## F1 — Tela inicial (Home) com menu de navegação
- **Onde:** `:app` (`HomeScreen`, `MainActivity`).
- **O que faz:** exibe um menu com os botões "Carteira" e "Transferência".
- **Como navega:** cada botão dispara um **deep link** (`Intent ACTION_VIEW`),
  abrindo a Activity do módulo correspondente — demonstrando navegação entre
  Activities de módulos diferentes.

## F2 — Abertura de módulos por deep link
- **Onde:** `:navigation` (`DeepLinkFactory`/`DeepLinkParser`), manifests dos
  features (`intent-filter`).
- **O que faz:** resolve URLs `app://library/wallet` e
  `app://library/transfer?...` para as Activities corretas.
- **Destaque:** funciona tanto a partir da Home quanto de um disparo externo
  (ex.: `adb shell am start ...`).

## F3 — Carteira: navegação entre composables
- **Onde:** `:feature:wallet`.
- **Fluxo:** `Menu` → `Minhas contas` → `Detalhe da conta`.
- **Passagem de dados entre composables:** o `accountId` selecionado na lista é
  passado como **argumento da rota** `AccountDetail(accountId)`, e o detalhe
  resolve a conta via `WalletData.byId(...)`.
- **Dados:** três contas em memória (Conta Corrente, Poupança, Investimentos).

## F4 — Transferência: fluxo com confirmação e recibo
- **Onde:** `:feature:transfer`.
- **Fluxo:** `Formulário` → `Confirmação` → `Recibo`.
- **Passagem de dados entre composables:** `amount` e `toAccount` viajam do
  formulário para a confirmação e para o recibo como argumentos das rotas.
- **`replaceTop`:** ao confirmar, a confirmação é substituída pelo recibo, de
  modo que "voltar" não retorne ao passo já concluído.
- **Validação:** o botão "Continuar" só habilita com valor numérico positivo e
  conta de destino preenchida.

## F5 — Passagem de informação entre Activities via deep link
- **Onde:** `:app` (emissor) → `:feature:transfer` (receptor).
- **O que faz:** a Home pode sugerir uma transferência já preenchida
  (`amount=50.0`, `toAccount=ACC-2`); esses valores viajam **na URL do deep link**
  e pré-preenchem o formulário na outra Activity/módulo.

## F6 — Back stack e botão voltar
- **Onde:** `Navigator` + `NavDisplay.onBack` em cada `*NavHost`.
- **O que faz:** o "voltar" desempilha a rota atual; na raiz do módulo, encerra a
  Activity (`finish()`), devolvendo o controle a quem a abriu.

## F7 — Acessibilidade
- **Onde:** todos os composables (`HomeScreen`, `*Screens.kt`).
- **O que faz:** títulos marcados como `heading()`, descrições de conteúdo em
  elementos interativos e agrupamento semântico de itens compostos.

## F8 — Suíte de testes para estudo
- **Onde:** `src/test` de cada módulo.
- **O que faz:** fornece exemplos de testes de unidade (JUnit + MockK) e de UI na
  JVM (Robolectric + Compose Test) para servir de base ao implementar os demais.
- Ver [05-cobertura-de-testes.md](05-cobertura-de-testes.md) para o que ainda
  falta cobrir.

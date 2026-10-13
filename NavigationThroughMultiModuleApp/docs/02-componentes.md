# 02 — Componentes implementados

Lista de cada componente, sua **localização**, a **camada arquitetural** a que
pertence e sua **responsabilidade**.

## Módulo `:navigation` (compartilhado)

| Componente | Arquivo | Camada | Responsabilidade |
|-----------|---------|--------|------------------|
| `AppDestination` | `navigation/src/main/java/com/br/navigation/AppDestination.kt` | Contrato / navegação de nível app | `sealed interface` com os destinos alcançáveis por deep link (`Wallet`, `Transfer(amount, toAccount)`). |
| `DeepLinkParser` | `navigation/.../DeepLinkParser.kt` | Navegação (compartilhada) | Converte uma URL (`String`) em `AppDestination`. Kotlin/JVM puro (sem `android.net.Uri`). |
| `DeepLinkFactory` | `navigation/.../DeepLinkFactory.kt` | Navegação (compartilhada) | Converte um `AppDestination` em URL. Inverso do parser; usado pelo `:app`. |
| `Navigator` | `navigation/.../Navigator.kt` | Navegação (compartilhada) | Gerencia o back stack do Nav3 (`SnapshotStateList<NavKey>`): `goTo`, `goBack`, `replaceTop`, `canGoBack`, `current`. É a peça central da **DI manual**. |

## Módulo `:app`

| Componente | Arquivo | Camada | Responsabilidade |
|-----------|---------|--------|------------------|
| `MainActivity` | `app/.../MainActivity.kt` | Entrada (Android) | Hospeda a `HomeScreen`. Lado **emissor** do deep link: `deepLinkIntent()` monta `Intent(ACTION_VIEW)` com a URL da `DeepLinkFactory`. |
| `HomeScreen` | `app/.../HomeScreen.kt` | Apresentação (UI) | Menu inicial acessível com botões "Carteira" e "Transferência". |
| `NavigationThroughMultiModuleAppTheme` | `app/.../ui/theme/Theme.kt` | Apresentação (tema) | Tema Material 3 (gerado pelo template). |
| `Color` / `Type` | `app/.../ui/theme/{Color,Type}.kt` | Apresentação (tema) | Paleta e tipografia (gerados pelo template). |

## Módulo `:feature:wallet` (Carteira)

| Componente | Arquivo | Camada | Responsabilidade |
|-----------|---------|--------|------------------|
| `WalletActivity` | `feature/wallet/.../WalletActivity.kt` | Entrada (Android) | Entrypoint. Lado **receptor** do deep link `app://library/wallet`; valida o destino e monta o `WalletNavHost`. |
| `WalletNavHost` | `feature/wallet/.../WalletNavHost.kt` | Navegação (feature) | `NavDisplay` + `entryProvider` que resolve cada `WalletRoute`. Também expõe `rememberWalletNavigator()`. |
| `WalletRoute` | `feature/wallet/.../WalletRoute.kt` | Navegação (feature) | `sealed interface : NavKey` com as rotas internas: `Menu`, `Accounts`, `AccountDetail(accountId)`. |
| `WalletMenuScreen` / `AccountsScreen` / `AccountDetailScreen` | `feature/wallet/.../WalletScreens.kt` | Apresentação (UI) | Telas acessíveis do fluxo da carteira. `formatBrl()` auxilia a formatação. |
| `Account` / `WalletData` | `feature/wallet/.../WalletData.kt` | Dados (didática) | Modelo de conta e fonte de dados em memória. |

## Módulo `:feature:transfer` (Transferência)

| Componente | Arquivo | Camada | Responsabilidade |
|-----------|---------|--------|------------------|
| `TransferActivity` | `feature/transfer/.../TransferActivity.kt` | Entrada (Android) | Entrypoint. Lado **receptor** do deep link `app://library/transfer`; extrai `amount`/`toAccount` e pré-preenche o formulário. |
| `TransferNavHost` | `feature/transfer/.../TransferNavHost.kt` | Navegação (feature) | `NavDisplay` + `entryProvider` do fluxo. Expõe `rememberTransferNavigator(startRoute)`. |
| `TransferRoute` | `feature/transfer/.../TransferRoute.kt` | Navegação (feature) | `sealed interface : NavKey`: `Form(prefill...)`, `Confirmation(amount, toAccount)`, `Receipt(amount, toAccount)`. |
| `TransferFormScreen` / `TransferConfirmationScreen` / `TransferReceiptScreen` | `feature/transfer/.../TransferScreens.kt` | Apresentação (UI) | Telas acessíveis do fluxo de transferência. `formatBrl()` auxilia a formatação. |

## Mapa rápido camada → arquivos

```
Entrada (Android / entrypoints)
  app/MainActivity.kt
  feature/wallet/WalletActivity.kt
  feature/transfer/TransferActivity.kt

Navegação (feature)
  feature/wallet/WalletNavHost.kt, WalletRoute.kt
  feature/transfer/TransferNavHost.kt, TransferRoute.kt

Navegação (compartilhada)
  navigation/Navigator.kt, AppDestination.kt, DeepLinkParser.kt, DeepLinkFactory.kt

Apresentação (UI)
  app/HomeScreen.kt
  feature/wallet/WalletScreens.kt
  feature/transfer/TransferScreens.kt
  app/ui/theme/*

Dados (didática)
  feature/wallet/WalletData.kt
```

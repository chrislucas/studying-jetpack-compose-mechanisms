# 01 — Arquitetura

## Visão geral

O projeto é um app Android **multi-módulo** organizado por **feature**, com um
módulo transversal de **navegação**. A navegação acontece em dois níveis:

1. **Entre módulos (nível app):** cada feature é uma `Activity` exportada que
   responde a um **deep link**. O `:app` dispara `Intent`s de deep link para
   abrir essas Activities. Isso simula o mundo real em que diferentes partes de
   um app (ou apps diferentes) se comunicam via `Intent`/URI.

2. **Dentro do módulo (nível feature):** cada feature usa o **Navigation 3**
   (`NavDisplay` + back stack) para navegar entre composables/telas.

```
┌──────────────────────────────────────────────────────────────┐
│                            :app                                │
│  MainActivity  ──(Intent ACTION_VIEW app://library/...)──┐     │
│  HomeScreen (menu)                                       │     │
└──────────────────────────────────────────────────────────┼────┘
                                                            │ deep link
                 ┌──────────────────────────────┬──────────┘
                 ▼                               ▼
┌──────────────────────────┐      ┌──────────────────────────────┐
│     :feature:wallet       │      │      :feature:transfer        │
│  WalletActivity           │      │  TransferActivity             │
│   └ WalletNavHost         │      │   └ TransferNavHost           │
│       (NavDisplay)        │      │       (NavDisplay)            │
│   Menu → Accounts → Detail│      │   Form → Confirmation → Receipt│
└──────────────┬───────────┘      └───────────────┬──────────────┘
               │                                   │
               └──────────────┬────────────────────┘
                              ▼
               ┌──────────────────────────────┐
               │          :navigation          │
               │  Navigator (back stack)       │
               │  AppDestination (contrato)    │
               │  DeepLinkParser / Factory     │
               └──────────────────────────────┘
```

## Módulos e suas responsabilidades

| Módulo | Tipo Gradle | Responsabilidade | Depende de |
|--------|-------------|------------------|------------|
| `:app` | `com.android.application` | Tela inicial (Home) e disparo de deep links. Agrega os features no APK final. | `:navigation`, `:feature:wallet`, `:feature:transfer` |
| `:navigation` | `com.android.library` | Abstrações de navegação compartilhadas: `Navigator`, contrato de deep link (`AppDestination`) e parser/factory de URL. | — (apenas Nav3 runtime + serialization) |
| `:feature:wallet` | `com.android.library` | Feature Carteira: Activity de deep link + telas internas. | `:navigation` |
| `:feature:transfer` | `com.android.library` | Feature Transferência: Activity de deep link + telas internas. | `:navigation` |

Regras de dependência:

- Features **nunca** dependem umas das outras (desacoplamento).
- Features e `:app` dependem de `:navigation` (o "contrato comum").
- `:navigation` não depende de ninguém (é a base) e é mantido **o mais puro
  possível** (parser em Kotlin/JVM puro, sem `android.net.Uri`), o que o torna
  rápido de testar.

## Camadas (dentro de um feature)

Cada feature segue uma separação simples em camadas, adequada ao foco do projeto
(navegação) sem introduzir complexidade desnecessária:

```
Camada de entrada (Android)      → *Activity.kt       (entrypoint + deep link)
Camada de navegação (feature)    → *NavHost.kt, *Route.kt
Camada de apresentação (UI)      → *Screens.kt        (composables acessíveis)
Camada de dados (didática)       → *Data.kt           (dados em memória)
Camada compartilhada             → módulo :navigation (Navigator, deep link)
```

> Observação: por ser um projeto de estudo focado em navegação, **não há** camada
> de domínio/repositório/rede. Os dados são fornecidos em memória (`WalletData`).
> Esse é um ponto natural de extensão caso você queira evoluir o projeto.

## Fluxo de um deep link ponta a ponta

Exemplo: usuário toca em "Transferência" na Home.

1. `HomeScreen` chama o callback `onOpenTransfer`.
2. `MainActivity.deepLinkIntent(...)` usa `DeepLinkFactory.urlFor(Transfer(...))`
   para montar a URL `app://library/transfer?amount=50.0&toAccount=ACC-2` e cria
   um `Intent(ACTION_VIEW)`.
3. O Android resolve o `Intent` pelo `intent-filter` do `TransferActivity`
   (declarado no manifest de `:feature:transfer`).
4. `TransferActivity` lê `intent.data`, usa `DeepLinkParser.parse(...)` para obter
   `AppDestination.Transfer(amount, toAccount)` e cria a rota inicial
   `TransferRoute.Form(prefilled...)`.
5. `TransferNavHost` monta o `NavDisplay` a partir do back stack do `Navigator`
   e exibe o formulário já pré-preenchido.
6. A partir daí, a navegação entre telas é feita empilhando rotas no back stack
   (`Form → Confirmation → Receipt`), passando dados como argumentos das rotas.

## Decisões de build relevantes

- **AGP 9** aplica o Kotlin de forma embutida; por isso os módulos de biblioteca
  **não** aplicam `org.jetbrains.kotlin.android` (causaria conflito de extensão).
- As tasks de teste usam **Java 17** por compatibilidade com o Robolectric
  (ver nota em [README](README.md)).

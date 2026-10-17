### Tabela Comparativa: Navigation Compose vs. Navigation 3

| Critério / Funcionalidade | Navigation Compose (Nav2) | Navigation 3 (Nav3) |
| :--- | :--- | :--- |
| **Propriedade da Back Stack** | **Interna da biblioteca**:<br>Controlada de forma imperativa pelo `NavController` por meio de caixas-pretas ocultas. | **Propriedade do desenvolvedor**:<br>A pilha é simplesmente um `SnapshotStateList<NavKey>` que você manipula diretamente como qualquer lista do Kotlin. |
| **Modelo de Navegação** | **Baseado em Eventos**:<br>Você dispara comandos (`navController.navigate("route")`) e aguarda o framework atualizar o estado internamente. | **Baseado em Estado**:<br>Segue o fluxo padrão do Compose. Alterar o estado da lista/pilha dispara automaticamente a recomposição da tela. |
| **Componente Visual Principal** | **`NavHost`**:<br>O container central que gerencia os destinos internamente com base no controlador. | **`NavDisplay`**:<br>Um componente declarativo que apenas observa a lista da back stack fornecida e renderiza as telas equivalentes. |
| **Gerenciamento de Ciclo de Vida** | **Acoplado ao Ciclo de Vida Android**:<br>Cada destino possui um `LifecycleOwner` próprio gerenciado internamente pelas entradas do `NavHost` (`NavBackStackEntry`). | **Acoplado à Composição**:<br>O ciclo de vida é atrelado diretamente à presença da `NavEntry` na árvore de composição, alinhando-se estritamente aos efeitos colaterais (`DisposableEffect`, `LaunchedEffect`) do Compose. |
| **Definição de Rotas e Tipagem** | **Strings ou Type-Safe recente**:<br>Historicamente dependente de rotas baseadas em String. Suporte a tipagem foi adicionado tardiamente. | **Type-Safe nativo**:<br>Totalmente baseado em chaves (`NavKey`) e objetos/data classes marcados obrigatoriamente com `@Serializable`. |
| **Manipulação Avançada da Pilha** | **Limitada**:<br>Difícil reordenar, limpar posições intermediárias ou manipular o histórico sem causar inconsistências. | **Totalmente Flexível**:<br>Permite operações complexas na lista como `add`, `removeLast`, inverter (`reverse`) ou organizar destinos diretamente. |
| **Layouts Adaptáveis (Multi-pane)** | **Complexo**:<br>Criar fluxos de Lista-Detalhes simultâneos para tablets e dobráveis exigia contornos e "hacks" no `NavHost`. | **Nativo**:<br>A arquitetura permite que layouts leiam múltiplos destinos da back stack simultaneamente via *Scenes API* para telas grandes. |
| **Escopo de ViewModels** | **Acoplado ao Grafo**:<br>Vinculado a rotas específicas ou ao ciclo de vida gerenciado estritamente pelas entradas do `NavHost`. | **Escopo Flexível**:<br>Permite vincular instâncias de `ViewModel` diretamente a uma `NavEntry` específica contida na back stack. |
| **Suporte Multiplataforma** | **Foco primário em Android**:<br>Portado parcialmente pela comunidade e pela JetBrains para o Compose Multiplatform. | **Nativo Multiplataforma**:<br>Projetado em conjunto para o ecossistema Android e Compose Multiplatform (iOS, Desktop, Web). |

```
// Definição das rotas com Type-Safe (Kotlin Serialization)
@Serializable object Home
@Serializable data class Profile(val userId: String)

@Composable
fun AppNavigation() {
val navController = rememberNavController()

    // O NavHost gerencia e esconde a pilha de telas de forma imperativa
    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            HomeScreen(
                onNavigateToProfile = { id -> 
                    // Evento imperativo que altera o estado interno da biblioteca
                    navController.navigate(Profile(userId = id)) 
                }
            )
        }
        composable<Profile> { backStackEntry ->
            val profile = backStackEntry.toRoute<Profile>()
            ProfileScreen(userId = profile.userId)
        }
    }
}

// Definição das chaves/destinos de navegação
@Serializable object HomeKey
@Serializable data class ProfileKey(val userId: String)

@Composable
fun AppNavigation() {
// A pilha é sua! É apenas uma lista observável gerenciada no estado do Compose
val backStack = rememberSaveableStateHolderList { listOf(HomeKey) }

    // O NavDisplay apenas reage às mudanças da lista 'backStack'
    NavDisplay(
        backStack = backStack,
        entryProvider = { key ->
            when (key) {
                is HomeKey -> NavEntry(key) {
                    HomeScreen(
                        onNavigateToProfile = { id ->
                            // Adiciona um novo elemento diretamente à lista do Kotlin
                            backStack.add(ProfileKey(userId = id))
                        }
                    )
                }
                is ProfileKey -> NavEntry(key) {
                    // Os parâmetros já estão fortemente tipados no objeto 'key'
                    ProfileScreen(userId = key.userId)
                }
            }
        }
    )
}

```

No Navigation 3, o ciclo de vida é governado puramente pelas fases de Composição 
e Descarte do Jetpack Compose,
tornando o uso de blocos como LaunchedEffect e DisposableEffect a maneira padrão e nativa 
de capturar quando uma tela entra ou sai de foco.


```
@Composable
fun ProfileScreen(userId: String) {
    // 1. Captura o momento em que a tela entra na árvore de composição (On Resume / On Start)
    LaunchedEffect(userId) {
        println("Ciclo de Vida: Tela do usuário $userId foi aberta e renderizada.")
        // Ótimo lugar para disparar requisições de API ou analytics de visualização
    }

    // 2. Captura o ciclo completo, incluindo o momento exato em que ela é destruída (On Destroy / On Pause)
    DisposableEffect(userId) {
        println("Ciclo de Vida: Inicializando recursos da tela.")

        onDispose {
            println("Ciclo de Vida: Tela do usuário $userId foi removida da Back Stack.")
            // Ideal para cancelar listeners, limpar caches locais ou salvar estados temporários
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Perfil do Usuário: $userId")
    }
}
```

## Por que isso é diferente do Navigation antigo?

No Navigation Compose antigo, as telas podiam permanecer vivas na memória (em background na pilha) 
mantendo o estado de composição ativo. Para escutar o ciclo de vida real do Android,
você precisava registrar um LifecycleEventObserver na NavBackStackEntry.

No Navigation 3, se uma NavEntry é removida da lista da sua Back Stack, ela deixa de ser 
composta imediatamente, disparando o bloco onDispose de forma nativa e previsível.


## Prós e Contras: Tomada de Decisão

### Navigation Compose (Nav2)

#### Prós
* **Maturidade do ecossistema**: Biblioteca amplamente testada em produção há anos por milhares de aplicativos.
* **Abundância de materiais**: Vasta quantidade de tutoriais, tópicos no StackOverflow e artigos da comunidade disponíveis para resolver problemas comuns.
* **Ferramental maduro**: Suporte consolidado de bibliotecas de terceiros para Injeção de Dependência (como Hilt e Koin).

#### Contras
* **Caixa-preta imperativa**: Falta de controle direto sobre o histórico e a pilha de telas, gerando problemas ao tentar fazer manipulações complexas.
* **Suporte Multiplataforma adaptado**: Não foi desenhado originalmente para rodar fora do Android, o que causa limitações no Compose Multiplatform.
* **Complexidade adaptativa**: Dificuldade crônica para lidar de forma elegante com designs multi-pane (telas grandes/dobráveis).

---

### Navigation 3 (Nav3)

#### Prós
* **Controle total do desenvolvedor**: A pilha é um estado puro (`SnapshotStateList`), dando liberdade total para você ler, modificar e reordenar o histórico.
* **Nativamente declarativo**: Totalmente alinhado com a filosofia do Jetpack Compose, eliminando a ponte imperativa do sistema de Views antigo.
* **Foco em Multiplataforma**: Projetado desde o primeiro dia com suporte de primeira classe para Android, iOS, Desktop e Web.
* **Segurança de tipos nativa**: Chaves serializáveis obrigatórias que evitam erros em tempo de execução com Strings mal formatadas.

#### Contras
* **Curva de aprendizado inicial**: Exige uma mudança de mentalidade (Mental Model) para desenvolvedores acostumados a gerenciar rotas por texto ou comandos imperativos.
* **Maturidade tecnológica**: Por ser a abordagem mais recente, pode apresentar menor volume de discussões na comunidade ou requerer atualizações frequentes de APIs.
* **Adaptação de DI**: Pode exigir pequenas configurações manuais extras ao integrar escopos de injeção de dependência muito específicos voltados à antiga NavBackStackEntry.



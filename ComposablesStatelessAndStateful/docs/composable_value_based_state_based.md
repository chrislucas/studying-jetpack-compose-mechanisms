# Value-Based e State-Based


```kotlin
// Exemplo de abordagem Baseada em Valor
@Composable
fun ValueBasedInput() {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text, // Passa o valor cru
        onValueChange = { text = it } // Sincronização manual obrigatória
    )
}

```

```kotlin
// Exemplo de abordagem Baseada em Estado
@Composable
fun StateBasedInput() {
    val state = rememberTextFieldState() // Objeto gerenciador dedicado

    TextField(
        state = state // O componente gerencia a leitura/escrita internamente
    )
}

```


| Característica | Componente Baseado em Valor (Value-based) | Componente Baseado em Estado (State-based) |
| :--- | :--- | :--- |
| **Parâmetros de Entrada** | Um valor primitivo/data class (`T`) + callback (`(T) -> Unit`). | Um objeto de estado dedicado (`StateHolder`). |
| **Sincronização** | Manual. Você deve interceptar o evento e atualizar a variável. | Automática e encapsulada dentro do objeto de estado. |
| **Performance/Estabilidade** | Pode sofrer com atrasos e travamentos em atualizações assíncronas. | Mais robusto; evita condições de corrida em digitação rápida. |
| **Uso Recomendado** | Componentes puramente visuais e simples (ex: `Text`, `Icon`). | Componentes interativos complexos (ex: formulários, editores de texto). |

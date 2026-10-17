# Diferença entre funções composable diferença de stateless e stateful

- Em Jetpack Compose, a diferença essencial entre um componente (composable) Stateless 
e um Stateful está em quem gerencia e armazena o estado daquele elemento de interface

## 🏛️ Visão Geral

- Stateless: Uma função que nao possui nem gerencia nenhum estado interno. Ela apenas
recebe os dados atuais por meio de parâmetros e avisa quando o usuario interage

- Stateful: Uma função que guarda e gerencia seu próprio estado internamente, geralmente
utilizando remember { mutableStateOf() } 


## 🔄 Comparação Direta


| Característica | Composable Stateless 🟢 | Composable Stateful 🔴 |
| :--- | :--- | :--- |
| **Armazenamento de Estado** | Não possui estado interno. Recebe os dados por argumentos. | Armazena e gerencia o estado internamente via `remember`. |
| **Comunicação / Eventos** | Usa callbacks (`lambdas`) para avisar o pai sobre mudanças. | Modifica o próprio estado diretamente dentro da função. |
| **Reutilização do Código** | Altíssima. Pode ser usado em qualquer tela com dados diferentes. | Baixa. Fica preso à lógica de negócio ou estado que gerencia. |
| **Facilidade de Testes** | Muito fácil. Basta testar a entrada de dados e a saída de eventos. | Mais complexa. Exige interações na UI para simular mudanças de estado. |
| **Padrão Recomendado** | Ideal para componentes visuais da UI (botões, cards, campos). | Usado geralmente no topo da tela para conectar com ViewModels. |
| :--- | :--- | :--- |
| **Armazenamento** | Recebe valores por parâmetros (`value: T`). | Mantém o próprio estado internamente (`remember`). |
| **Eventos** | Dispara callbacks (`onValueChange: (T) -> Unit`). | Modifica suas variáveis diretamente na própria função. |
| **Reutilização** | Muito alta. Funciona em qualquer lugar do app. | Baixa. Fica acoplado à lógica interna que ele gerencia. |
| **Testabilidade** | Fácil. Basta passar dados fixos e testar o clique. | Mais difícil. Requer simulação de fluxo e UI complexa. |

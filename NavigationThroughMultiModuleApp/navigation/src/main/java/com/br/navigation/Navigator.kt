package com.br.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

/**
 * Gerencia o back stack do Navigation 3 **dentro de um módulo/Activity**.
 *
 * Esta é a peça central da nossa "DI manual": cada feature cria um [Navigator]
 * (normalmente no `onCreate` da Activity) com sua rota inicial e o repassa para
 * os composables. Não há framework de DI — o [Navigator] é apenas um objeto
 * que você constrói e passa adiante.
 *
 * O back stack é um [SnapshotStateList], então o `NavDisplay` recompõe
 * automaticamente quando ele muda. As rotas devem implementar [NavKey] e ser
 * `@Serializable` para poderem ser salvas/restauradas.
 *
 * @param startDestination a primeira rota exibida (normalmente o menu do módulo).
 */
class Navigator(startDestination: NavKey) {

    val backStack: SnapshotStateList<NavKey> = mutableStateListOf(startDestination)

    /** Rota atualmente no topo do back stack. */
    val current: NavKey
        get() = backStack.last()

    /** Empilha um novo destino (navega para frente). */
    fun goTo(destination: NavKey) {
        backStack.add(destination)
    }

    /**
     * Substitui o topo do back stack pelo destino informado.
     * Útil, por exemplo, ao confirmar uma transferência e seguir para o recibo
     * sem permitir voltar para o formulário já submetido.
     */
    fun replaceTop(destination: NavKey) {
        if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.lastIndex)
        }
        backStack.add(destination)
    }

    /**
     * Desempilha o topo (volta uma tela).
     *
     * @return `true` se havia algo para desempilhar e ainda sobrou pelo menos
     * uma entrada; `false` se já estávamos na raiz (o chamador deve então
     * encerrar a Activity, por exemplo).
     */
    fun goBack(): Boolean {
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    /** `true` se é possível voltar sem sair do módulo. */
    fun canGoBack(): Boolean = backStack.size > 1
}

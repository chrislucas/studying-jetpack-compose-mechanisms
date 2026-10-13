package com.br.shortcutmenu

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider

fun SemanticsNodeInteractionsProvider.onNodeWithRole(
    role: Role
): SemanticsNodeInteraction = onNode(
    SemanticsMatcher.expectValue(
        SemanticsProperties.Role, role
    )
)
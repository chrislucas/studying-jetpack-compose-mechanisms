package com.br.shortcutmenu.core

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat

fun Context.shortcutBuilder(id: String, name: String) = ShortcutInfoCompat.Builder(this, name)

fun ShortcutInfoCompat.Builder.withShortLabel(shortLabel: String) = setShortLabel(shortLabel)

fun ShortcutInfoCompat.Builder.withLongLabel(longLabel: String) = setLongLabel(longLabel)

fun ShortcutInfoCompat.Builder.withIcon(icon: IconCompat) = setIcon(icon)

fun ShortcutInfoCompat.Builder.withIntent(intent: Intent) = setIntent(intent)

fun ShortcutInfoCompat.Builder.create() = build()


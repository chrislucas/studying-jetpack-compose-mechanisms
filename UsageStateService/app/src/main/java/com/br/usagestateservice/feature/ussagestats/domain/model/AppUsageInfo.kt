package com.br.usagestateservice.feature.ussagestats.domain.model

import android.graphics.drawable.Drawable

data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val usageTimeMinutes: Long,
    val appIcon: Drawable?
)

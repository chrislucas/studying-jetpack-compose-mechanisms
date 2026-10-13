package com.br.contentprovider.feature.techdictonary.domain

import android.net.Uri
import androidx.core.net.toUri
import com.br.contentprovider.feature.techdictonary.core.provider.TechDictionaryProvider.Companion.AUTHORITY

object TechDictionaryContract {
    val CONTENT_URI: Uri = "content://$AUTHORITY/terms".toUri()

    const val COLUMN_ID = "_id"
    const val COLUMN_TERM = "term"
    const val COLUMN_DEFINITION = "definition"
}
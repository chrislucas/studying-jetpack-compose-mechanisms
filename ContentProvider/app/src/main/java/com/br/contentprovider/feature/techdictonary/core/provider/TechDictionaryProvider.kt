package com.br.contentprovider.feature.techdictonary.core.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri

class TechDictionaryProvider: ContentProvider() {

    companion object {
        const val AUTHORITY = "com.br.contentprovider.feature.techdictonary.core.provider"

    }

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/vnd.$AUTHORITY.terms"


    // Implementações vazias para focar apenas na leitura por enquanto
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
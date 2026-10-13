package com.br.contentprovider.feature.techdictonary.core.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.br.contentprovider.feature.techdictonary.core.provider.TechDictionaryProvider.Companion.AUTHORITY
import com.br.contentprovider.feature.techdictonary.domain.TechDictionaryContract
import com.br.contentprovider.feature.techdictonary.domain.TechDictionaryContract.AUTHORITY

class MockedTechDictionaryProvider : ContentProvider() {

    // Para esta etapa didática, usaremos um MatrixCursor com dados estáticos.
    // Na próxima iteração, podemos plugar o Room ou SQLiteOpenHelper aqui.
    private val mockData = listOf(
        listOf(1, "Robolectric", "Framework que permite rodar testes do Android na JVM."),
        listOf(
            2,
            "Semantics",
            "Árvore de propriedades usada por serviços de acessibilidade no Compose."
        ),
        listOf(3, "MockK", "Biblioteca de mocking nativa para Kotlin.")
    )

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? {
        if (uri != TechDictionaryContract.CONTENT_URI) return null

        val cursor = MatrixCursor(
            arrayOf(
                TechDictionaryContract.COLUMN_ID,
                TechDictionaryContract.COLUMN_TERM,
                TechDictionaryContract.COLUMN_DEFINITION
            )
        )

        val queryTerm = selectionArgs?.firstOrNull()?.lowercase()

        mockData.forEach { row ->
            val term = row[1] as String
            if (queryTerm == null || term.lowercase().contains(queryTerm)) {
                cursor.addRow(row)
            }
        }

        return cursor
    }

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
package com.br.shortcutmenu.core

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.startup.Initializer

/*
    Qual a melhor estratégia, custom application ou Initializer ? quais os prós e contras?
 */
class SettingRepositoryInitializer : Initializer<SettingRepository> {

    override fun create(context: Context): SettingRepository {
        return SettingRepository(
            /*
                Cria a instancia unica de Datastore e a passa para sua classe dependente
             */
            PreferenceDataStoreFactory.create {
                context.preferencesDataStoreFile("settings")
            }
        )
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        /*
            retorna uma lista vazia caso o componente nao dependa de outro inicializador do App Startup/
         */
        return emptyList()
    }
}
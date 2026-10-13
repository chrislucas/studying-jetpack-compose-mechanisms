package com.br.shortcutmenu.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SettingRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        val SAMPLE_TOGGLE_CONFIG_KEY = booleanPreferencesKey("sample_toggle_config")
    }

    /*
        Lê a informacao salva. Padrao=false
     */
    val configEnabled: Flow<Boolean> = dataStore.data.map { structure ->
        structure[SAMPLE_TOGGLE_CONFIG_KEY] ?: false
    }

    suspend fun saveConfig(newValue: Boolean) {
        dataStore.edit { structure ->
            structure[SAMPLE_TOGGLE_CONFIG_KEY] = newValue
        }
    }
}
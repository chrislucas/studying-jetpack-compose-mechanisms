package com.br.configflow

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigFlowImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig?
) : ConfigFlow {

    init {
        remoteConfig?.let {
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600 // Example: 1 hour
            }
            it.setConfigSettingsAsync(configSettings)
        }
    }

    override suspend fun fetchAndActivate(): Boolean {
        val config = remoteConfig ?: return false
        return try {
            config.fetchAndActivate().await()
        } catch (e: Exception) {
            // Handle error or log it
            false
        }
    }

    override fun getString(key: String): String = remoteConfig?.getString(key) ?: ""

    override fun getBoolean(key: String): Boolean = remoteConfig?.getBoolean(key) ?: false

    override fun getLong(key: String): Long = remoteConfig?.getLong(key) ?: 0L
}

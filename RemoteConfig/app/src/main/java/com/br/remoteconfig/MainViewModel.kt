package com.br.remoteconfig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.configflow.ConfigFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FetchStatus {
    data object Idle : FetchStatus
    data object Loading : FetchStatus
    data object Success : FetchStatus
    data class Error(val message: String) : FetchStatus
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val configFlow: ConfigFlow
) : ViewModel() {

    private val _status = MutableStateFlow<FetchStatus>(FetchStatus.Idle)
    val status: StateFlow<FetchStatus> = _status.asStateFlow()

    private val _configValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val configValues: StateFlow<Map<String, String>> = _configValues.asStateFlow()

    fun fetchConfig() {
        viewModelScope.launch {
            _status.value = FetchStatus.Loading
            try {
                val result = configFlow.fetchAndActivate()
                if (result) {
                    _status.value = FetchStatus.Success
                    updateConfigValues()
                } else {
                    _status.value = FetchStatus.Error("Failed to fetch and activate")
                }
            } catch (e: Exception) {
                _status.value = FetchStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateConfigValues() {
        _configValues.value = mapOf(
            "api_key" to configFlow.getString("api_key"),
            "feature_enabled" to configFlow.getBoolean("feature_enabled").toString(),
            "max_items" to configFlow.getLong("max_items").toString()
        )
    }
}

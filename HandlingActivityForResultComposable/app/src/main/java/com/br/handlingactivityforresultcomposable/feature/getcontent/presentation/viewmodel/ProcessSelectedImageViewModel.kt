package com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


sealed class SelectedImageState {

    open val content: String
        get() = when (this) {
            is ProcessedSelectedImageUiState -> this.content
            else -> ""
        }
    object Idle : SelectedImageState()
    object ProcessingSelectedImage : SelectedImageState()
    data class ProcessedSelectedImageUiState(override val content: String) : SelectedImageState()
}


/*
    https://share.google/aimode/x5U07a27dGRyCP6p0
 */
class ProcessSelectedImageViewModel(private val application: Application) : ViewModel() {

    init {
        println("ProcessSelectedImageViewModel init")
    }


    private val _uiState = MutableStateFlow<SelectedImageState>(SelectedImageState.Idle)
    val uiState: StateFlow<SelectedImageState> = _uiState.asStateFlow()

    fun processSelectedImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = SelectedImageState.ProcessingSelectedImage
            delay(1000L.milliseconds) // Simulate processing delay
            processImageUri(imageUri)
        }
    }

    private suspend fun processImageUri(imageUri: Uri) = withContext(Dispatchers.IO) {
        application.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            inputStream.bufferedReader().use { reader ->
                val content = reader.readText()
                _uiState.value = SelectedImageState.ProcessedSelectedImageUiState(content = content)
            }
        }
    }

    companion object {
        val FACTORY = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application
                ProcessSelectedImageViewModel(application)
            }
        }
    }
}
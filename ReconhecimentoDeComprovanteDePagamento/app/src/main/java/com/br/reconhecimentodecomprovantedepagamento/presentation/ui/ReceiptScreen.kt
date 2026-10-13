package com.br.reconhecimentodecomprovantedepagamento.presentation.ui

import android.graphics.BitmapFactory
import android.os.Build
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.reconhecimentodecomprovantedepagamento.domain.model.ReceiptCategory
import com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel.ReceiptUiState
import com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel.ReceiptViewModel
import com.br.reconhecimentodecomprovantedepagamento.presentation.viewmodel.ReceiptViewModelFactory

/**
 * Tela principal do classificador de comprovantes.
 *
 * Usa o Android Photo Picker (zero-permissão) para selecionar a imagem do
 * comprovante, decodifica para [android.graphics.Bitmap] e aciona o
 * [ReceiptViewModel]. A UI reage ao [ReceiptUiState] via `collectAsStateWithLifecycle`.
 */
@Composable
fun ReceiptScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel: ReceiptViewModel = viewModel(
        factory = ReceiptViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val pickMedia = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = decodeBitmap(context, uri)
            if (bitmap != null) {
                viewModel.classify(bitmap)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Classificador de Comprovantes",
            style = MaterialTheme.typography.headlineSmall
        )

        Button(
            onClick = {
                pickMedia.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Text("Selecionar comprovante")
        }

        when (val state = uiState) {
            ReceiptUiState.Idle -> {
                Text(
                    text = "Selecione a imagem de um comprovante para classificá-lo.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            ReceiptUiState.Processing -> {
                CircularProgressIndicator()
                Text("Processando OCR e classificação…")
            }

            is ReceiptUiState.Success -> {
                ResultCard(
                    category = state.category,
                    score = state.score,
                    rawText = state.rawText
                )
            }

            is ReceiptUiState.Error -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Erro",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultCard(
    category: ReceiptCategory,
    score: Float,
    rawText: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Categoria: ${category.name}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Confiança: ${"%.1f".format(score * 100)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Texto reconhecido:",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = rawText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Decodifica o [Uri] selecionado em um [android.graphics.Bitmap] mutável/software. */
private fun decodeBitmap(context: android.content.Context, uri: Uri) =
    runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                // ML Kit requer um bitmap em software (não HARDWARE).
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }.getOrNull()

package com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.viewmodel.ProcessSelectedImageViewModel
import com.br.handlingactivityforresultcomposable.feature.getcontent.presentation.viewmodel.SelectedImageState
import com.br.handlingactivityforresultcomposable.ui.theme.HandlingActivityForResultComposableTheme

/*
    https://share.google/aimode/x5U07a27dGRyCP6p0
 */

@Composable
fun SelectImageScreen(
    modifier: Modifier = Modifier,
    viewModel: ProcessSelectedImageViewModel = viewModel(
        factory = ProcessSelectedImageViewModel.FACTORY
    )
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { imageUri ->
        imageUri?.let {
            viewModel.processSelectedImage(imageUri)
        }
    }

    Box(modifier = modifier) {
        when (val content = uiState) {
            is SelectedImageState.ProcessingSelectedImage -> {
                LoadingOverlayScreen()
                SelectImageContent(
                    imageUri = content.content,
                    onSelectImageClick = { launcher.launch("image/*") },
                    modifier = Modifier
                )
            }

            else -> {
                SelectImageContent(
                    imageUri = content.content,
                    onSelectImageClick = { launcher.launch("image/*") },
                    modifier = Modifier
                )
            }
        }
    }

}

@Composable
fun SelectImageContent(
    modifier: Modifier = Modifier,
    imageUri: String?,
    onSelectImageClick: () -> Unit
) {
    Column(modifier = modifier) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onSelectImageClick,
            shape = RectangleShape
        ) {
            Text(text = "Select Image")
        }

        Text(text = "Selected Image URI: \"${imageUri ?: "No image selected"}\"")
    }
}

@Preview(showBackground = true)
@Composable
fun SelectImageScreenPreview() {
    HandlingActivityForResultComposableTheme {
        SelectImageContent(
            modifier = Modifier.padding(16.dp),
            imageUri = null,
            onSelectImageClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SelectImageScreenWithImagePreview() {
    HandlingActivityForResultComposableTheme {
        SelectImageContent(
            modifier = Modifier.padding(16.dp),
            imageUri = "content://media/external/images/media/123",
            onSelectImageClick = {}
        )
    }
}

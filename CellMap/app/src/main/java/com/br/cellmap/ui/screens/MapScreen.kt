package com.br.cellmap.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.br.cellmap.data.remote.model.OpenCellIdResponse
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    userLocation: Location?,
    towerLocation: OpenCellIdResponse?,
    nearbyTowers: List<OpenCellIdResponse>,
    isLoading: Boolean,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tower Map") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        val initialPos = userLocation?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(0.0, 0.0)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(initialPos, 12f)
        }

        // Update camera when user location is first available
        LaunchedEffect(userLocation) {
            userLocation?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 12f)
            }
        }

        Box(modifier = modifier
            .fillMaxSize()
            .padding(innerPadding)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = userLocation != null)
        ) {
            // Nearby towers
            nearbyTowers.forEach { tower ->
                if (tower.lat != 0.0 && tower.lon != 0.0) {
                    val towerLatLng = LatLng(tower.lat, tower.lon)
                    Marker(
                        state = rememberUpdatedMarkerState(position = towerLatLng),
                        title = "Nearby Tower",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
            }

            // Main tower
            towerLocation?.let { tower ->
                if (tower.lat != 0.0 && tower.lon != 0.0) {
                    val towerLatLng = LatLng(tower.lat, tower.lon)
                    Marker(
                        state = rememberUpdatedMarkerState(position = towerLatLng),
                        title = "Connected Tower",
                        snippet = "Range: ${tower.range}m",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                    Circle(
                        center = towerLatLng,
                        radius = tower.range.toDouble(),
                        fillColor = Color.Blue.copy(alpha = 0.2f),
                        strokeColor = Color.Blue,
                        strokeWidth = 2f
                    )
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (towerLocation == null && !isLoading) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "Tower location not found",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        }
    }
}

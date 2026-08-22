package com.br.cellmap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirective
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.br.cellmap.data.LocationRepositoryImpl
import com.br.cellmap.data.TelephonyRepositoryImpl
import com.br.cellmap.data.model.CellData
import com.br.cellmap.ui.components.PermissionWrapper
import com.br.cellmap.ui.navigation.CellInfoRoute
import com.br.cellmap.ui.navigation.MapRoute
import com.br.cellmap.ui.screens.MapScreen
import com.br.cellmap.ui.theme.CellMapTheme
import com.br.cellmap.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val telephonyRepository = TelephonyRepositoryImpl(applicationContext)
        val locationRepository = LocationRepositoryImpl(applicationContext)
        
        setContent {
            CellMapTheme {
                PermissionWrapper {
                    val viewModel: MainViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return MainViewModel(telephonyRepository, locationRepository) as T
                            }
                        }
                    )
                    
                    val backStack = rememberNavBackStack(CellInfoRoute)
                    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
                    val directive = remember(windowAdaptiveInfo) {
                        calculatePaneScaffoldDirective(windowAdaptiveInfo)
                            .copy(horizontalPartitionSpacerSize = 0.dp)
                    }
                    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(directive = directive)

                    NavDisplay(
                        backStack = backStack,
                        onBack = { backStack.removeLastOrNull() },
                        sceneStrategy = listDetailStrategy,
                        entryProvider = entryProvider {
                            entry<CellInfoRoute>(
                                metadata = ListDetailSceneStrategy.listPane(
                                    detailPlaceholder = {
                                        val towerLocation by viewModel.towerLocation.collectAsState()
                                        val nearbyTowers by viewModel.nearbyTowers.collectAsState()
                                        val userLocation by viewModel.userLocation.collectAsState()
                                        val isLoading by viewModel.isLoadingTower.collectAsState()
                                        MapScreen(
                                            userLocation = userLocation,
                                            towerLocation = towerLocation,
                                            nearbyTowers = nearbyTowers,
                                            isLoading = isLoading
                                        )
                                    }
                                )
                            ) {
                                CellInfoScreen(
                                    viewModel = viewModel,
                                    onOpenMap = {
                                        (backStack as MutableList<NavKey>).add(MapRoute)
                                    }
                                )
                            }
                            entry<MapRoute>(
                                metadata = ListDetailSceneStrategy.detailPane()
                            ) {
                                val towerLocation by viewModel.towerLocation.collectAsState()
                                val nearbyTowers by viewModel.nearbyTowers.collectAsState()
                                val userLocation by viewModel.userLocation.collectAsState()
                                val isLoading by viewModel.isLoadingTower.collectAsState()
                                MapScreen(
                                    userLocation = userLocation,
                                    towerLocation = towerLocation,
                                    nearbyTowers = nearbyTowers,
                                    isLoading = isLoading,
                                    onBack = { backStack.removeLastOrNull() }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CellInfoScreen(
    viewModel: MainViewModel,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cellData by viewModel.cellData.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CellMap") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenMap,
                icon = { Icon(Icons.Rounded.Map, contentDescription = null) },
                text = { Text("Show Map") }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            cellData?.let { data ->
                AdaptiveDashboard(data)
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No registered cell info found. Make sure you have a SIM card and are connected to a network.",
                        modifier = Modifier.padding(32.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun AdaptiveDashboard(data: CellData) {
    val windowAdaptiveInfo = currentWindowAdaptiveInfoV2()
    // Simplified expanded check to avoid unresolved reference
    val isExpanded = windowAdaptiveInfo.windowSizeClass.windowWidthSizeClass.toString().contains("Expanded")

    if (isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SignalCard(data)
                NetworkIdentityCard(data)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TowerInfoCard(data)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SignalCard(data)
            NetworkIdentityCard(data)
            TowerInfoCard(data)
        }
    }
}

@Composable
fun SignalCard(data: CellData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.SignalCellularAlt,
                    contentDescription = "Signal Level ${data.signalLevel}",
                    tint = when (data.signalLevel) {
                        4 -> MaterialTheme.colorScheme.primary
                        3 -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Text(
                    text = "Signal Quality",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            InfoRow(label = "Signal Strength", value = "${data.signalDbm ?: "N/A"} dBm")
            InfoRow(label = "Signal Level", value = "${data.signalLevel}/4")
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { data.signalLevel / 4f },
                modifier = Modifier.fillMaxWidth(),
                color = when (data.signalLevel) {
                    4 -> MaterialTheme.colorScheme.primary
                    3 -> MaterialTheme.colorScheme.secondary
                    2 -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                },
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun NetworkIdentityCard(data: CellData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.Dns, contentDescription = null)
                Text(
                    text = "Network Identity",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(label = "Network Type", value = data.networkType)
            InfoRow(label = "MCC", value = data.mcc ?: "N/A")
            InfoRow(label = "MNC", value = data.mnc ?: "N/A")
        }
    }
}

@Composable
fun TowerInfoCard(data: CellData) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.CellTower, contentDescription = null)
                Text(
                    text = "Tower Info",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoRow(label = "LAC/TAC", value = data.lacTac?.toString() ?: "N/A")
            InfoRow(label = "Cell ID", value = data.cellId?.toString() ?: "N/A")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}

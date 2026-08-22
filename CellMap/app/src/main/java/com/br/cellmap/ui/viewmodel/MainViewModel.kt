package com.br.cellmap.ui.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.cellmap.data.LocationRepository
import com.br.cellmap.data.TelephonyRepository
import com.br.cellmap.data.model.CellData
import com.br.cellmap.data.remote.model.OpenCellIdResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val telephonyRepository: TelephonyRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    val cellData: StateFlow<CellData?> = telephonyRepository.getCellData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val userLocation: StateFlow<Location?> = locationRepository.getUserLocation()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _towerLocation = MutableStateFlow<OpenCellIdResponse?>(null)
    val towerLocation: StateFlow<OpenCellIdResponse?> = _towerLocation.asStateFlow()

    private val _nearbyTowers = MutableStateFlow<List<OpenCellIdResponse>>(emptyList())
    val nearbyTowers: StateFlow<List<OpenCellIdResponse>> = _nearbyTowers.asStateFlow()

    private val _isLoadingTower = MutableStateFlow(false)
    val isLoadingTower: StateFlow<Boolean> = _isLoadingTower.asStateFlow()

    init {
        viewModelScope.launch {
            cellData.collectLatest { data ->
                if (data != null && data.mcc != null && data.mnc != null && data.lacTac != null && data.cellId != null) {
                    fetchTowerLocation(data.mcc, data.mnc, data.lacTac, data.cellId)
                }
            }
        }

        viewModelScope.launch {
            userLocation.collectLatest { location ->
                location?.let {
                    fetchNearbyTowers(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun fetchTowerLocation(mcc: String, mnc: String, lac: Int, cellId: Long) {
        viewModelScope.launch {
            _isLoadingTower.value = true
            val response = telephonyRepository.getTowerLocation(mcc, mnc, lac, cellId)
            _towerLocation.value = response
            _isLoadingTower.value = false
        }
    }

    private fun fetchNearbyTowers(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = telephonyRepository.getNearbyTowers(lat, lon)
            _nearbyTowers.value = response
        }
    }
}

package com.br.samplegeocodingapi.feature.simple.fromgeoloctozipcode.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.br.samplegeocodingapi.feature.simple.fromgeoloctozipcode.presentation.state.ZipCodeStata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ZipCodeViewModel : ViewModel() {

    private val zipCodeMutableState: MutableStateFlow<ZipCodeStata> = MutableStateFlow(
        ZipCodeStata(false, "")
    )

    private val zipCodeState: StateFlow<ZipCodeStata> = zipCodeMutableState.asStateFlow()

}
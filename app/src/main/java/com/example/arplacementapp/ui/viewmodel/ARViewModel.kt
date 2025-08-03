package com.example.arplacementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.arplacementapp.data.model.Drill
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ARViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ARUiState())
    val uiState: StateFlow<ARUiState> = _uiState.asStateFlow()

    fun setCurrentDrill(drill: Drill) {
        _uiState.value = _uiState.value.copy(currentDrill = drill)
    }

    fun setPlaneDetected(detected: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaneDetected = detected)
    }

    fun setObjectPlaced(placed: Boolean) {
        _uiState.value = _uiState.value.copy(isObjectPlaced = placed)
    }

    fun updateInstructions(instructions: String) {
        _uiState.value = _uiState.value.copy(instructions = instructions)
    }
}

data class ARUiState(
    val currentDrill: Drill? = null,
    val isPlaneDetected: Boolean = false,
    val isObjectPlaced: Boolean = false,
    val instructions: String = "Move your device to detect surfaces"
)
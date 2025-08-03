package com.example.arplacementapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.arplacementapp.data.model.Drill
import com.example.arplacementapp.data.repository.DrillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ARViewModel @Inject constructor(
    private val drillRepository: DrillRepository
) : ViewModel() {

    init {
        Log.d("ARViewModel", "ARViewModel created with repository: $drillRepository")
    }

    private val _uiState = MutableStateFlow(ARUiState())
    val uiState: StateFlow<ARUiState> = _uiState.asStateFlow()

    fun setCurrentDrill(drill: Drill) {
        Log.d("ARViewModel", "Setting current drill: ${drill.name}")
        _uiState.value = _uiState.value.copy(
            currentDrill = drill,
            instructions = "Move your device to detect horizontal surfaces"
        )
    }

    fun setCurrentDrillById(drillId: Int) {
        Log.d("ARViewModel", "Setting current drill by ID: $drillId")
        val drill = drillRepository.getDrillById(drillId)
        drill?.let { setCurrentDrill(it) }
    }

    fun setPlaneDetected(detected: Boolean) {
        Log.d("ARViewModel", "Plane detected: $detected")
        _uiState.value = _uiState.value.copy(
            isPlaneDetected = detected,
            instructions = if (detected && !_uiState.value.isObjectPlaced) {
                "Tap on the detected surface to place ${_uiState.value.currentDrill?.name ?: "drill"}"
            } else _uiState.value.instructions
        )
    }

    fun setObjectPlaced(placed: Boolean) {
        Log.d("ARViewModel", "Object placed: $placed")
        val currentState = _uiState.value
        val newCount = if (placed && !currentState.isObjectPlaced) {
            currentState.placedObjectsCount + 1
        } else if (!placed) {
            0 // Reset count when starting fresh
        } else {
            currentState.placedObjectsCount
        }

        _uiState.value = currentState.copy(
            isObjectPlaced = placed,
            placedObjectsCount = newCount,
            instructions = when {
                placed -> "${currentState.currentDrill?.name ?: "Drill"} placed successfully! Tap anywhere to place another."
                currentState.isPlaneDetected -> "Tap on detected surface to place ${currentState.currentDrill?.name ?: "drill"}"
                else -> "Move your device to detect horizontal surfaces"
            }
        )
    }

    fun updateInstructions(instructions: String) {
        Log.d("ARViewModel", "Updating instructions: $instructions")
        _uiState.value = _uiState.value.copy(instructions = instructions)
    }

    fun resetARState() {
        Log.d("ARViewModel", "Resetting AR state")
        val currentDrill = _uiState.value.currentDrill
        _uiState.value = ARUiState(
            currentDrill = currentDrill,
            instructions = "Move your device to detect horizontal surfaces"
        )
    }

    fun incrementPlacedObjects() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            placedObjectsCount = currentState.placedObjectsCount + 1,
            isObjectPlaced = true,
            instructions = "${currentState.currentDrill?.name ?: "Drill"} placed! Total: ${currentState.placedObjectsCount + 1}. Tap to place another."
        )
    }

    fun getPlacementInstructions(): String {
        val currentState = _uiState.value
        return when {
            !currentState.isPlaneDetected -> "Move your device to detect horizontal surfaces"
            currentState.placedObjectsCount == 0 -> "Tap on detected surface to place your first ${currentState.currentDrill?.name ?: "drill"}"
            else -> "Great! ${currentState.placedObjectsCount} objects placed. Tap to place another ${currentState.currentDrill?.name ?: "drill"}"
        }
    }
}

data class ARUiState(
    val currentDrill: Drill? = null,
    val isPlaneDetected: Boolean = false,
    val isObjectPlaced: Boolean = false,
    val placedObjectsCount: Int = 0,
    val instructions: String = "Move your device to detect horizontal surfaces",
    val isARSessionActive: Boolean = false,
    val errorMessage: String? = null
)
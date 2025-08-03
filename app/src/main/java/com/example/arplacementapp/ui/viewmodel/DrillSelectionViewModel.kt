package com.example.arplacementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arplacementapp.data.model.Drill
import com.example.arplacementapp.data.repository.DrillRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrillSelectionViewModel @Inject constructor(
    private val drillRepository: DrillRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DrillSelectionUiState())
    val uiState: StateFlow<DrillSelectionUiState> = _uiState.asStateFlow()

    init {
        loadDrills()
    }

    private fun loadDrills() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                drills = drillRepository.getAllDrills(),
                isLoading = false
            )
        }
    }

    fun selectDrill(drill: Drill) {
        _uiState.value = _uiState.value.copy(selectedDrill = drill)
    }
}

data class DrillSelectionUiState(
    val drills: List<Drill> = emptyList(),
    val selectedDrill: Drill? = null,
    val isLoading: Boolean = true
)
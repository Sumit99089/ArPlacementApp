package com.example.arplacementapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arplacementapp.ui.components.DrillCard
import com.example.arplacementapp.ui.viewmodel.DrillSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrillSelectionScreen(
    onDrillSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DrillSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select a Drill",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.drills) { drill ->
                    DrillCard(
                        drill = drill,
                        isSelected = uiState.selectedDrill?.id == drill.id,
                        onClick = {
                            viewModel.selectDrill(drill)
                            onDrillSelected(drill.id)
                        }
                    )
                }
            }
        }
    }
}
package com.example.arplacementapp.ui.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.arplacementapp.data.repository.DrillRepository
import com.example.arplacementapp.ui.components.ARSceneView
import com.example.arplacementapp.ui.viewmodel.ARViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ARScreen(
    drillId: Int,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ARViewModel = hiltViewModel() // ✅ Only inject ViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // ✅ Load drill via ViewModel (which uses injected repository)
    LaunchedEffect(drillId) {
        Log.d("ARScreen", "Loading drill with ID: $drillId")
        viewModel.setCurrentDrillById(drillId)
    }

    if (!cameraPermissionState.status.isGranted) {
        CameraPermissionScreen(
            permissionState = cameraPermissionState,
            onNavigateBack = onNavigateBack
        )
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        // AR Scene View
        ARSceneView(
            drill = uiState.currentDrill, // ✅ Get drill from ViewModel state
            onPlaneDetected = { detected ->
                viewModel.setPlaneDetected(detected)
                if (detected && !uiState.isObjectPlaced) {
                    viewModel.updateInstructions("Tap on the ground to place ${uiState.currentDrill?.name ?: "drill"} marker")
                }
            },
            onObjectPlaced = { placed ->
                viewModel.setObjectPlaced(placed)
                if (placed) {
                    viewModel.updateInstructions("${uiState.currentDrill?.name ?: "Drill"} marker placed successfully!")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Rest of your UI code stays the same...
        TopAppBar(
            title = {
                Text(
                    text = uiState.currentDrill?.name ?: "AR Drill",
                    color = Color.White
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.5f)
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Instructions Panel
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                if (uiState.isPlaneDetected && !uiState.isObjectPlaced) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Status Indicators
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp, 80.dp, 16.dp, 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(
                text = "Plane",
                isActive = uiState.isPlaneDetected
            )
            StatusChip(
                text = "Object",
                isActive = uiState.isObjectPlaced
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun CameraPermissionScreen(
    permissionState: PermissionState,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app needs camera access to provide AR functionality. Please grant camera permission to continue.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { permissionState.launchPermissionRequest() }
        ) {
            Text("Grant Permission")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Go Back")
        }
    }
}

@Composable
private fun StatusChip(
    text: String,
    isActive: Boolean
) {
    AssistChip(
        onClick = { },
        label = { Text(text) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            labelColor = if (isActive)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        )
    )
}
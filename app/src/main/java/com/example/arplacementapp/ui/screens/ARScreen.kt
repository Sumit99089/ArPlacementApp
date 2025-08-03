package com.example.arplacementapp.ui.screens

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
    viewModel: ARViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Load drill via ViewModel
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
            drill = uiState.currentDrill,
            onPlaneDetected = { detected ->
                viewModel.setPlaneDetected(detected)
                if (detected && !uiState.isObjectPlaced) {
                    viewModel.updateInstructions("Tap on screen to place ${uiState.currentDrill?.name ?: "drill"}")
                }
            },
            onObjectPlaced = { placed ->
                viewModel.setObjectPlaced(placed)
                if (placed) {
                    viewModel.updateInstructions("${uiState.currentDrill?.name ?: "Drill"} placed successfully!")
                } else {
                    viewModel.updateInstructions("Tap on screen to place ${uiState.currentDrill?.name ?: "drill"}")
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top App Bar
        TopAppBar(
            title = {
                Column {
                    Text(
                        text = uiState.currentDrill?.name ?: "AR Drill",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Difficulty: ${uiState.currentDrill?.difficulty?.name ?: ""}",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
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
            actions = {
                IconButton(onClick = {
                    // Reset AR session
                    viewModel.resetARState()
                }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black.copy(alpha = 0.6f)
            ),
            modifier = Modifier.align(Alignment.TopCenter)
        )
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
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📷",
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Camera Permission Required",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "This app needs camera access to provide AR functionality. Please grant camera permission to continue with the AR drill experience.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { permissionState.launchPermissionRequest() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Grant Camera Permission")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}
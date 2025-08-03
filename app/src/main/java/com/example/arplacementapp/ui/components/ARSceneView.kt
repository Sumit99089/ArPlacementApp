package com.example.arplacementapp.ui.components

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.arplacementapp.data.model.Drill
import kotlinx.coroutines.delay

@Composable
fun ARSceneView(
    drill: Drill?,
    onPlaneDetected: (Boolean) -> Unit,
    onObjectPlaced: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isObjectPlaced by remember { mutableStateOf(false) }
    var isPlaneDetected by remember { mutableStateOf(false) }

    // Simulate plane detection after a delay
    LaunchedEffect(Unit) {
        delay(3000) // Simulate 3 seconds to detect plane
        isPlaneDetected = true
        onPlaneDetected(true)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(surfaceProvider)

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            }
        )

        // AR Overlay
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Text
            Text(
                text = when {
                    !isPlaneDetected -> "Move device to detect surfaces..."
                    !isObjectPlaced -> "Tap on the screen to place ${drill?.name ?: "object"}"
                    else -> "${drill?.name ?: "Object"} placed successfully!"
                },
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            )

            // Tap target (only show when plane is detected and object not placed)
            if (isPlaneDetected && !isObjectPlaced) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.8f))
                        .clickable {
                            isObjectPlaced = true
                            onObjectPlaced(true)
                        }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(getDrillObjectSize(drill))
                            .background(
                                getDrillObjectColor(drill),
                                when (drill?.id) {
                                    1 -> MaterialTheme.shapes.small // Square
                                    2 -> MaterialTheme.shapes.medium // Rounded
                                    3 -> CircleShape // Circle
                                    else -> MaterialTheme.shapes.small
                                }
                            )
                    )
                }
            }

            // Placed object indicator
            if (isObjectPlaced) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            getDrillObjectColor(drill),
                            when (drill?.id) {
                                1 -> MaterialTheme.shapes.small
                                2 -> MaterialTheme.shapes.medium
                                3 -> CircleShape
                                else -> MaterialTheme.shapes.small
                            }
                        )
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun getDrillObjectColor(drill: Drill?): Color {
    return when (drill?.id) {
        1 -> Color.Green
        2 -> Color.Blue
        3 -> Color.Red
        else -> Color.Yellow
    }
}

private fun getDrillObjectSize(drill: Drill?): androidx.compose.ui.unit.Dp {
    return when (drill?.id) {
        1 -> 30.dp
        2 -> 25.dp
        3 -> 35.dp
        else -> 30.dp
    }
}
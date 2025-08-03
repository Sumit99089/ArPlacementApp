package com.example.arplacementapp.ui.components

import android.util.Log
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
    var placedObjects by remember { mutableStateOf<List<PlacedObject>>(emptyList()) }

    // Simulate plane detection after a delay
    LaunchedEffect(Unit) {
        delay(2000) // Simulate 2 seconds to detect plane
        isPlaneDetected = true
        onPlaneDetected(true)
        Log.d("ARSceneView", "Simulated plane detection")
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview as background
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build()
                            preview.setSurfaceProvider(surfaceProvider)
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            Log.e("ARSceneView", "Camera setup failed", e)
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
                    !isPlaneDetected -> "Scanning for surfaces..."
                    placedObjects.isEmpty() -> "Tap anywhere to place ${drill?.name ?: "drill"}"
                    else -> "${drill?.name ?: "Drill"} placed! Tap to place another."
                },
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.8f),
                        MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
            )

            // Placement area
            if (isPlaneDetected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clickable {
                            // Add new placed object (only allow one as per assignment)
                            val newObject = PlacedObject(
                                id = System.currentTimeMillis(),
                                drill = drill,
                                x = (50..250).random(),
                                y = (50..150).random()
                            )
                            placedObjects = listOf(newObject) // Only one object at a time
                            isObjectPlaced = true
                            onObjectPlaced(true)
                            Log.d("ARSceneView", "Object placed for drill: ${drill?.name}")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Show placed objects
                    placedObjects.forEach { placedObject ->
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = placedObject.x.dp,
                                    y = placedObject.y.dp
                                )
                                .size(getDrillObjectSize(placedObject.drill))
                                .background(
                                    getDrillObjectColor(placedObject.drill),
                                    getDrillObjectShape(placedObject.drill)
                                )
                                .clickable {
                                    // Remove object on click
                                    placedObjects = emptyList()
                                    isObjectPlaced = false
                                    onObjectPlaced(false)
                                }
                        )
                    }

                    // Placement indicator when no objects are placed
                    if (placedObjects.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(
                                    Color.White.copy(alpha = 0.3f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(getDrillObjectSize(drill))
                                        .background(
                                            getDrillObjectColor(drill),
                                            getDrillObjectShape(drill)
                                        )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "TAP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Control buttons
            if (placedObjects.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            MaterialTheme.shapes.medium
                        )
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    androidx.compose.material3.Button(
                        onClick = {
                            placedObjects = emptyList()
                            isObjectPlaced = false
                            onObjectPlaced(false)
                        }
                    ) {
                        Text("Place Another")
                    }

                    Text(
                        text = "Object Placed",
                        color = Color.Green,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Drill info overlay
        if (drill != null) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp, 80.dp, 16.dp, 16.dp)
                    .width(140.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = drill.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = drill.difficulty.name,
                        style = MaterialTheme.typography.labelSmall,
                        color = getDrillObjectColor(drill)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                getDrillObjectColor(drill),
                                getDrillObjectShape(drill)
                            )
                    )
                }
            }
        }

        // Surface detection indicator
        if (isPlaneDetected) {
            Text(
                text = "Surface Detected ✓",
                color = Color.Green,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp, 80.dp, 16.dp, 16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        MaterialTheme.shapes.small
                    )
                    .padding(8.dp)
            )
        }
    }
}

data class PlacedObject(
    val id: Long,
    val drill: Drill?,
    val x: Int,
    val y: Int
)

private fun getDrillObjectColor(drill: Drill?): Color {
    return when (drill?.id) {
        1 -> Color(0xFF4CAF50) // Green
        2 -> Color(0xFF2196F3) // Blue
        3 -> Color(0xFFF44336) // Red
        else -> Color(0xFFFFEB3B) // Yellow
    }
}

private fun getDrillObjectSize(drill: Drill?): androidx.compose.ui.unit.Dp {
    return when (drill?.id) {
        1 -> 40.dp // Basic Positioning
        2 -> 35.dp // Advanced Movement
        3 -> 45.dp // Expert Technique
        else -> 40.dp
    }
}

private fun getDrillObjectShape(drill: Drill?): androidx.compose.foundation.shape.CornerBasedShape {
    return when (drill?.id) {
        1 -> androidx.compose.foundation.shape.RoundedCornerShape(4.dp) // Square for basic
        2 -> androidx.compose.foundation.shape.RoundedCornerShape(12.dp) // Rounded for advanced
        3 -> CircleShape // Circle for expert
        else -> androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
    }
}
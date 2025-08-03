package com.example.arplacementapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arplacementapp.ui.screens.ARScreen
import com.example.arplacementapp.ui.screens.DrillDetailScreen
import com.example.arplacementapp.ui.screens.DrillSelectionScreen
import com.example.arplacementapp.ui.theme.ArPlacementAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "MainActivity created - Hilt should be working")
        setContent {
            ArPlacementAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "drill_selection"
                    ) {
                        composable("drill_selection") {
                            DrillSelectionScreen(
                                onDrillSelected = { drillId ->
                                    navController.navigate("drill_detail/$drillId")
                                }
                            )
                        }

                        composable("drill_detail/{drillId}") { backStackEntry ->
                            val drillId = backStackEntry.arguments?.getString("drillId")?.toIntOrNull() ?: 0
                            DrillDetailScreen(
                                drillId = drillId,
                                onStartAR = { drillId ->
                                    navController.navigate("ar_scene/$drillId")
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("ar_scene/{drillId}") { backStackEntry ->
                            val drillId = backStackEntry.arguments?.getString("drillId")?.toIntOrNull() ?: 0
                            ARScreen(
                                drillId = drillId,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
package com.example.arplacementapp.data.repository

import android.util.Log
import com.example.arplacementapp.data.model.Drill
import com.example.arplacementapp.data.model.DrillDifficulty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrillRepository @Inject constructor() {

    init {
        Log.d("DrillRepository", "DrillRepository created via DI")
    }

    private val drills = listOf(
        Drill(
            id = 1,
            name = "Basic Positioning Drill",
            description = "Learn the fundamentals of proper positioning and stance. This drill focuses on building a solid foundation for all your movements.",
            tips = listOf(
                "Keep your feet shoulder-width apart",
                "Maintain proper posture throughout",
                "Focus on smooth, controlled movements",
                "Practice consistently for best results"
            ),
            imageRes = android.R.drawable.ic_menu_camera, // Temporary system drawable
            difficulty = DrillDifficulty.BEGINNER
        ),
        Drill(
            id = 2,
            name = "Advanced Movement Drill",
            description = "Master complex movement patterns with this challenging drill. Combines multiple techniques for enhanced coordination.",
            tips = listOf(
                "Start slowly and build up speed",
                "Focus on precision over speed",
                "Coordinate upper and lower body movements",
                "Take breaks when needed"
            ),
            imageRes = android.R.drawable.ic_menu_gallery, // Temporary system drawable
            difficulty = DrillDifficulty.INTERMEDIATE
        ),
        Drill(
            id = 3,
            name = "Expert Technique Drill",
            description = "Push your limits with this expert-level drill. Requires mastery of previous techniques and exceptional focus.",
            tips = listOf(
                "Ensure you've mastered prerequisite drills",
                "Maintain perfect form throughout",
                "Use visualization techniques",
                "Track your progress carefully"
            ),
            imageRes = android.R.drawable.ic_menu_info_details, // Temporary system drawable
            difficulty = DrillDifficulty.ADVANCED
        )
    )

    fun getAllDrills(): List<Drill> {
        Log.d("DrillRepository", "getAllDrills called, returning ${drills.size} drills")
        return drills
    }

    fun getDrillById(id: Int): Drill? {
        val drill = drills.find { it.id == id }
        Log.d("DrillRepository", "getDrillById($id) returning: ${drill?.name ?: "null"}")
        return drill
    }
}
package com.example.arplacementapp.data.repository

import com.example.arplacementapp.data.model.Drill
import com.example.arplacementapp.data.model.DrillDifficulty
import com.example.arplacementapp.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrillRepository @Inject constructor() {

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
            imageRes = R.drawable.ic_drill_1,
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
            imageRes = R.drawable.ic_drill_2,
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
            imageRes = R.drawable.ic_drill_3,
            difficulty = DrillDifficulty.ADVANCED
        )
    )

    fun getAllDrills(): List<Drill> = drills

    fun getDrillById(id: Int): Drill? = drills.find { it.id == id }
}
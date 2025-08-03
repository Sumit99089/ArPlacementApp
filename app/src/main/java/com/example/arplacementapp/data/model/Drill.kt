package com.example.arplacementapp.data.model

import androidx.annotation.DrawableRes

data class Drill(
    val id: Int,
    val name: String,
    val description: String,
    val tips: List<String>,
    @DrawableRes val imageRes: Int,
    val difficulty: DrillDifficulty = DrillDifficulty.BEGINNER
)

enum class DrillDifficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}
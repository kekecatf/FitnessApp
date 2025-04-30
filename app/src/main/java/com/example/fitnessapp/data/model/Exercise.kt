package com.example.fitnessapp.data.model

data class ExerciseResponse(
    val results: List<Exercise>
)

data class Exercise(
    val id: Int,
    val name: String?,
    val description: String?
)

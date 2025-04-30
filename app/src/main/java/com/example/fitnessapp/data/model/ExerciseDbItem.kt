package com.example.fitnessapp.data.model

// ExerciseDbItem.kt (Model)
data class ExerciseDbItem(
    val id: String,
    val name: String,
    val bodyPart: String,
    val target: String,
    val equipment: String,
    val gifUrl: String,
    val secondaryMuscles: List<String>?,
    val instructions: List<String>?
)

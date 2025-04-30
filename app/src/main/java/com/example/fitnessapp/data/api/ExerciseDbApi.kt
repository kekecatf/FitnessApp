package com.example.fitnessapp.data.api

import com.example.fitnessapp.data.model.ExerciseDbItem
import retrofit2.http.GET

interface ExerciseDbApi {
    @GET("exercises")
    suspend fun getExercises(): List<ExerciseDbItem>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @retrofit2.http.Path("bodyPart") bodyPart: String
    ): List<ExerciseDbItem>

}

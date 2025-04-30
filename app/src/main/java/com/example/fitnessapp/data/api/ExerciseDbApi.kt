package com.example.fitnessapp.data.api

import com.example.fitnessapp.data.model.ExerciseDbItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ExerciseDbApi.kt (API interface)
interface ExerciseDbApi {
    @GET("exercises")
    suspend fun getExercises(): List<ExerciseDbItem>

    @GET("exercises/bodyPartList")
    suspend fun getBodyPartList(): List<String>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun getExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): List<ExerciseDbItem>

}

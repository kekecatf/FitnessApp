package com.example.fitnessapp.data.api

import com.example.fitnessapp.data.model.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WgerApiService {
    @GET("exercise/")
    suspend fun getExercises(
        @Query("language") language: Int = 2, // 2 = English
        @Query("limit") limit: Int = 50
    ): ExerciseResponse
}

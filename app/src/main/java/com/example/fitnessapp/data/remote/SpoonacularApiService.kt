package com.example.fitnessapp.data.remote

import com.example.fitnessapp.data.model.FoodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SpoonacularApiService {
    @GET("recipes/complexSearch")
    suspend fun getFoodSuggestions(
        @Query("minCalories") minCalories: Int,
        @Query("maxCalories") maxCalories: Int,
        @Query("number") number: Int = 5,
        @Query("apiKey") apiKey: String
    ): FoodResponse
}

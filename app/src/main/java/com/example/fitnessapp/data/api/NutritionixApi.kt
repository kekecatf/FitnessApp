package com.example.fitnessapp.data.api

import com.example.fitnessapp.data.model.FoodRequest
import com.example.fitnessapp.data.model.NutritionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NutritionixApi {
    @POST("natural/nutrients")
    suspend fun getNutrition(
        @Body request: FoodRequest,
        @Header("x-app-id") appId: String = NutritionixConstants.APP_ID,
        @Header("x-app-key") apiKey: String = NutritionixConstants.API_KEY
    ): NutritionResponse
}


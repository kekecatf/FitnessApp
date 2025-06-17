package com.example.fitnessapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NutritionixClient {
    val api: NutritionixApi by lazy {
        Retrofit.Builder()
            .baseUrl(NutritionixConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NutritionixApi::class.java)
    }
}

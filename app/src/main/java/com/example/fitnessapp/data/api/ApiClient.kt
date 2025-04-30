package com.example.fitnessapp.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://exercisedb.p.rapidapi.com/"

    val apiService: ExerciseDbApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("X-RapidAPI-Key", "6400678744msh243929963a70e57p1f67ffjsnc8c52ce0f650")
                        .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ExerciseDbApi::class.java)
    }
}

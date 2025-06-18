package com.example.fitnessapp.data.projectfiles

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NoteApiClient {
    private const val BASE_URL = "https://mobilprogramlama.ardglobal.com.tr/fitness_takip/"


    val api: NoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NoteApiService::class.java)
    }
}

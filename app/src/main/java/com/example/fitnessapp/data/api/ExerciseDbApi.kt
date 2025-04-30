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
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ExerciseDbItem>

    @GET("exercises/equipmentList")
    suspend fun getEquipmentList(): List<String>

    @GET("exercises/targetList")
    suspend fun getTargetList(): List<String>

    @GET("exercises/equipment/{equipment}")
    suspend fun getExercisesByEquipment(
        @Path("equipment") equipment: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ExerciseDbItem>

    @GET("exercises/target/{target}")
    suspend fun getExercisesByTarget(
        @Path("target") target: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<ExerciseDbItem>
}


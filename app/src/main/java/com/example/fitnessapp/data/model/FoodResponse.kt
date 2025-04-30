package com.example.fitnessapp.data.model

data class FoodResponse(
    val results: List<FoodItem>
)

data class FoodItem(
    val id: Int,
    val title: String,
    val image: String
)

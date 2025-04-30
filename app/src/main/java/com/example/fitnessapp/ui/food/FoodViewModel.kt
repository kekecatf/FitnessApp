package com.example.fitnessapp.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.model.FoodItem
import com.example.fitnessapp.data.remote.SpoonacularApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FoodViewModel : ViewModel() {

    private val _foods = MutableStateFlow<List<FoodItem>>(emptyList())
    val foods: StateFlow<List<FoodItem>> = _foods
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val api: SpoonacularApiService = Retrofit.Builder()
        .baseUrl("https://api.spoonacular.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SpoonacularApiService::class.java)

    fun fetchFoodSuggestions(minCal: Int, maxCal: Int, apiKey: String) {
        viewModelScope.launch {
            try {
                val response = api.getFoodSuggestions(minCal, maxCal, 5, apiKey)
                _foods.value = response.results
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Bir hata oluştu"
            }

        }
    }
}

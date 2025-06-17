package com.example.fitnessapp.ui.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.api.NutritionixClient
import com.example.fitnessapp.data.model.FoodRequest
import com.example.fitnessapp.data.model.Nutrient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FoodViewModel : ViewModel() {
    private val _nutrients = MutableStateFlow<List<Nutrient>>(emptyList())
    val nutrients: StateFlow<List<Nutrient>> get() = _nutrients

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    fun fetchNutrition(query: String) {
        viewModelScope.launch {
            try {
                val response = NutritionixClient.api.getNutrition(FoodRequest(query))
                _nutrients.value = response.foods
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Hata: ${e.message}"
            }
        }

    }
}

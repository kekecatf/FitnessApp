package com.example.fitnessapp.ui.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.api.ApiClient
import com.example.fitnessapp.data.model.ExerciseDbItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ExerciseDbViewModel.kt
class ExerciseDbViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<ExerciseDbItem>>(emptyList())
    val exercises: StateFlow<List<ExerciseDbItem>> = _exercises.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _bodyParts = MutableStateFlow<List<String>>(emptyList())
    val bodyParts: StateFlow<List<String>> = _bodyParts.asStateFlow()

    fun fetchBodyParts() {
        viewModelScope.launch {
            try {
                val parts = ApiClient.apiService.getBodyPartList()
                _bodyParts.value = parts
            } catch (e: Exception) {
                _errorMessage.value = "Vücut bölgesi listesi alınamadı: ${e.message}"
            }
        }
    }

    fun fetchExercises(bodyPart: String? = null) {
        viewModelScope.launch {
            try {
                _exercises.value = if (bodyPart.isNullOrBlank()) {
                    ApiClient.apiService.getExercises()
                } else {
                    ApiClient.apiService.getExercisesByBodyPart(bodyPart)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Egzersizler alınamadı: ${e.message}"
            }
        }
    }
}



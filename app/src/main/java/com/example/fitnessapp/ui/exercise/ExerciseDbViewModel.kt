package com.example.fitnessapp.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.api.ApiClient
import com.example.fitnessapp.data.model.ExerciseDbItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseDbViewModel : ViewModel() {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _exercises = MutableStateFlow<List<ExerciseDbItem>>(emptyList())
    val exercises = _exercises.asStateFlow()

    private var allExercises: List<ExerciseDbItem> = emptyList()
    private var selectedBodyPart: String? = null
    private var selectedEquipment: String? = null
    private var selectedTarget: String? = null


    fun fetchExercises() {
        viewModelScope.launch {
            try {
                val result = ApiClient.apiService.getExercises()
                _exercises.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Bir hata oluştu"
            }

        }
    }

    fun setBodyPartFilter(value: String?) {
        selectedBodyPart = value
        applyFilters()
    }

    fun setEquipmentFilter(value: String?) {
        selectedEquipment = value
        applyFilters()
    }

    fun setTargetFilter(value: String?) {
        selectedTarget = value
        applyFilters()
    }

    private fun applyFilters() {
        _exercises.value = allExercises.filter { item ->
            (selectedBodyPart == null || item.bodyPart.equals(selectedBodyPart, ignoreCase = true)) &&
                    (selectedEquipment == null || item.equipment.equals(selectedEquipment, ignoreCase = true)) &&
                    (selectedTarget == null || item.target.equals(selectedTarget, ignoreCase = true))
        }
    }



}

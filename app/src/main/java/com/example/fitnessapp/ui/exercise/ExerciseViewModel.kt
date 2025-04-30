package com.example.fitnessapp.ui.exercise

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.api.ApiClient
import com.example.fitnessapp.data.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises = _exercises.asStateFlow()


//    fun fetchExercises(language: Int = 2) {
//        viewModelScope.launch {
//            _exercises.value = listOf(
//                Exercise(id = 1, name = "Push Up", description = "Push up açıklaması"),
//                Exercise(id = 2, name = "Squat", description = "Squat açıklaması")
//            )
//        }
//    }
    fun fetchExercises(language: Int = 2) {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getExercises(language = language)

                val filtered = response.results.filter {
                    !it.name.isNullOrBlank() && !it.description.isNullOrBlank()
                }

                filtered.forEach {
                    Log.d("EXERCISE", "name=${it.name} desc=${it.description}")
                }

                _exercises.value = filtered

            } catch (e: Exception) {
                Log.e("ExerciseViewModel", "API Error: ${e.localizedMessage}")
            }
        }
    }


}

package com.example.fitnessapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun saveProfile(
        height: Int,
        weight: Int,
        targetWeight: Int,
        gender: String,
        trainingDays: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("Kullanıcı oturumu bulunamadı.")
            return
        }

        val profileData = mapOf(
            "height" to height,
            "weight" to weight,
            "targetWeight" to targetWeight,
            "gender" to gender,
            "trainingDays" to trainingDays,
            "updatedAt" to System.currentTimeMillis()
        )

        _loading.value = true
        viewModelScope.launch {
            firestore.collection("users")
                .document(user.uid)
                .update(profileData)
                .addOnSuccessListener {
                    _loading.value = false
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    _loading.value = false
                    _error.value = exception.message
                    onError("Kayıt başarısız: ${exception.message}")
                }
        }
    }

    fun fetchProfile(
        onLoaded: (height: Int, weight: Int, targetWeight: Int, gender: String, trainingDays: Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onError("Oturum açmış kullanıcı yok.")
        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val height = doc.getLong("height")?.toInt() ?: 0
                val weight = doc.getLong("weight")?.toInt() ?: 0
                val targetWeight = doc.getLong("targetWeight")?.toInt() ?: 0
                val gender = doc.getString("gender") ?: ""
                val trainingDays = doc.getLong("trainingDays")?.toInt() ?: 0
                onLoaded(height, weight, targetWeight, gender, trainingDays)
            }
            .addOnFailureListener {
                onError("Veri alınamadı: ${it.message}")
            }
    }
}



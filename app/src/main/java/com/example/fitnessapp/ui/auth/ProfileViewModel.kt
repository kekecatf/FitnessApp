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

    private val _name = MutableStateFlow<String>("")
    val name: StateFlow<String> = _name


    fun saveProfile(
        name: String,
        lastName: String,
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
            "name" to name,
            "lastName" to lastName,
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
    fun loadUserProfile() {
        val uid = auth.currentUser?.uid ?: return
        _loading.value = true

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                _loading.value = false
                if (document != null && document.exists()) {
                    _name.value = document.getString("name") ?: ""
                }
            }
            .addOnFailureListener { e ->
                _loading.value = false
                _error.value = e.message
            }
    }
}



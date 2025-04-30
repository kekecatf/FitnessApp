package com.example.fitnessapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun registerUser(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        saveUserToFirestore()
                        onSuccess()
                    } else {
                        onError(task.exception?.message ?: "Kayıt Hatası")
                    }
                }
        }
    }



    fun loginUser(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onError(task.exception?.message ?: "Giriş Hatası")
                    }
                }
        }
    }
    fun saveUserToFirestore() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        val email = user.email ?: "no-email"
        val userData = hashMapOf(
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "Kullanıcı Firestore’a kaydedildi")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Hata: ${e.message}")
            }
    }

    private val firestore = FirebaseFirestore.getInstance()

}


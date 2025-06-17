package com.example.fitnessapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Email/şifre ile kayıt ol.
     * Başarılı olursa Firestore’a da kullanıcı belgesi eklenir.
     */
    fun registerUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore(email,
                        onComplete = onSuccess,
                        onError = onError
                    )
                } else {
                    onError(task.exception?.message ?: "Kayıt hatası")
                }
            }
    }

    /**
     * Email/şifre ile giriş yap.
     */
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(task.exception?.message ?: "Giriş hatası")
                }
            }
    }

    /**
     * Google One Tap vb. ile başarılı girişten sonra da çağırılabilir.
     * Eğer daha önce kayıt yapılmamışsa Firestore’a belge ekler.
     */
    fun loginWithGoogle(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = auth.currentUser
        if (user == null) {
            onError("Kullanıcı bulunamadı")
            return
        }

        saveUserToFirestore(
            email = user.email ?: "no-email@unknown.com",
            onComplete = onSuccess,
            onError = onError
        )
    }

    /**
     * Firestore’a kullanıcı belgesi ekler veya var olanı günceller.
     */
    private fun saveUserToFirestore(
        email: String,
        onComplete: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onError("UID alınamadı.")
            return
        }

        val userData = mapOf(
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(uid)
            .set(userData, SetOptions.merge()) // belge yoksa oluşturur
            .addOnSuccessListener {
                Log.d("Firestore", "Kullanıcı başarıyla kaydedildi/güncellendi.")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Firestore hatası: ${e.message}")
                onError("Kullanıcı kaydedilemedi: ${e.message}")
            }
    }

    /**
     * Firestore’da kullanıcı belgesi var mı kontrol eder.
     */
    fun checkUserProfileExists(
        onExists: () -> Unit,
        onNotExists: () -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onNotExists()
            return
        }

        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onExists()
                } else {
                    onNotExists()
                }
            }
            .addOnFailureListener {
                onNotExists()
            }
    }
}


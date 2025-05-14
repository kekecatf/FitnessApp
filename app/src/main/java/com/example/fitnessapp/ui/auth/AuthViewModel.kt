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
     * Başarılı olursa Firestore’a da kullanıcı belgesini oluştur.
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
                    // Kayıt başarılıysa Firestore’a ekle (merge=true ile upsert)
                    saveUserToFirestore(email) {
                        onSuccess()
                    }
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
        // Google ile ilk defa giriş yapıyorsa Firestore’da doküman yok demektir,
        // bu yüzden saveUserToFirestore çağırıyoruz.
        saveUserToFirestore(user.email ?: "") {
            onSuccess()
        }
    }

    /**
     * Firestore’a kullanıcı belgesini oluşturur veya var olanı merge eder.
     */
    private fun saveUserToFirestore(
        email: String,
        onComplete: () -> Unit = {}
    ) {
        val uid = auth.currentUser?.uid ?: return
        val userData = mapOf(
            "email" to email,
            "createdAt" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(uid)
            // SetOptions.merge() ile doküman yoksa oluşturur, varsa alanları ekler/günceller
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Kullanıcı kaydedildi/ güncellendi.")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Kaydetme hatası: ${e.message}")
            }
    }

    /**
     * Kullanıcı profil verilerini (boy, kilo, hedef kilo, cinsiyet, antrenman günü) Firestore’a yazar.
     */
    fun updateUserProfile(
        heightCm: Int,
        weightKg: Int,
        targetWeightKg: Int,
        gender: String,
        trainingDaysPerWeek: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onError("Önce giriş yapmalısınız.")
            return
        }
        val profileData = mapOf(
            "heightCm" to heightCm,
            "weightKg" to weightKg,
            "targetWeightKg" to targetWeightKg,
            "gender" to gender,
            "trainingDaysPerWeek" to trainingDaysPerWeek,
        )
        firestore.collection("users")
            .document(uid)
            .set(profileData, SetOptions.merge())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Profil güncelleme hatası") }
    }

    fun isUserAuthenticated(): Boolean = auth.currentUser != null
    fun signOut() = auth.signOut()
}

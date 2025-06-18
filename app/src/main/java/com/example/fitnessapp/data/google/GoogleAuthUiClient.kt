package com.example.fitnessapp.data.google

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.fitnessapp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signIn(): IntentSender? {
        val result = oneTapClient.beginSignIn(
            BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .setAutoSelectEnabled(true)
                .build()
        ).await()
        return result.pendingIntent.intentSender
    }

    fun signInWithIntent(
        intent: Intent,
        onResult: (Boolean, String?) -> Unit,
        onProfileMissing: () -> Unit
    ) {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val idToken = credential.googleIdToken

        if (idToken == null) {
            onResult(false, "ID Token alınamadı.")
            return
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val email = auth.currentUser?.email

                    if (uid != null && email != null) {
                        val userDocRef = firestore.collection("users").document(uid)

                        userDocRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // Kullanıcı profili zaten var
                                    onResult(true, null)
                                } else {
                                    // Profil yok, Firestore'a boş bir belge oluştur
                                    val userData = mapOf(
                                        "email" to email,
                                        "name" to "",  // İsteğe bağlı doldurulabilir
                                        "createdAt" to System.currentTimeMillis()
                                    )

                                    userDocRef.set(userData)
                                        .addOnSuccessListener {
                                            onProfileMissing() // yönlendir
                                        }
                                        .addOnFailureListener { e ->
                                            onResult(false, "Profil oluşturulamadı: ${e.message}")
                                        }
                                }
                            }
                            .addOnFailureListener { e ->
                                onResult(false, "Kullanıcı kontrol hatası: ${e.message}")
                            }
                    } else {
                        onResult(false, "Kullanıcı bilgileri alınamadı.")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

}
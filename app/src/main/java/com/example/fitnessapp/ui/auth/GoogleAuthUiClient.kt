package com.example.fitnessapp.ui.auth

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
                    if (uid != null) {
                        // Firestore'da kullanıcı profili var mı kontrolü
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    onResult(true, null)
                                } else {
                                    onProfileMissing()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Kullanıcı kontrol hatası: $e")
                                onResult(false, "Profil kontrol hatası")
                            }
                    } else {
                        onResult(false, "UID alınamadı.")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}

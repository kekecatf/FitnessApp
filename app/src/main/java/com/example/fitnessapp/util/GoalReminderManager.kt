package com.example.fitnessapp.util

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

object GoalReminderManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun shouldAskGoal(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val doc = firestore.collection("users").document(uid).get().await()
        val lastCheck = doc.getLong("lastGoalCheck") ?: return true // hiç yoksa sor
        val now = System.currentTimeMillis()
        val diff = now - lastCheck
        return diff > TimeUnit.DAYS.toMillis(7) // 7 günden fazlaysa tekrar sor
    }

    suspend fun saveGoal(goal: String) {
        val uid = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "weeklyGoal" to goal,
            "lastGoalCheck" to System.currentTimeMillis()
        )
        firestore.collection("users").document(uid).update(updates).await()
    }

    suspend fun getWeeklyGoal(): String? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = firestore.collection("users").document(uid).get().await()
        return doc.getString("weeklyGoal")
    }
}

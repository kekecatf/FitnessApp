package com.example.fitnessapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(navController: NavController) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Boy (cm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Kilo (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = targetWeight,
            onValueChange = { targetWeight = it },
            label = { Text("Hedef Kilo (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (user != null) {
                    val data = mapOf(
                        "height" to height.toIntOrNull(),
                        "weight" to weight.toIntOrNull(),
                        "targetWeight" to targetWeight.toIntOrNull()
                    )

                    firestore.collection("users")
                        .document(user.uid)
                        .update(data)
                        .addOnSuccessListener {
                            message = "Profil bilgileri kaydedildi."
                        }
                        .addOnFailureListener { e ->
                            message = "Hata: ${e.message}"
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message)
    }
}

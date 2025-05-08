package com.example.fitnessapp.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

// Ana ekranımız burada
@Composable
fun HomeScreen(navController: NavController) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Kullanıcı"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Hoş geldin, $userEmail",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeActionButton(text = "Profilini Güncelle") {
            navController.navigate("profile")
        }

        HomeActionButton(text = "Yemek Listesi") {
            navController.navigate("foods")
        }

        HomeActionButton(text = "Egzersiz Listesi") {
            navController.navigate("exercises")
        }

        HomeActionButton(text = "Çıkış Yap") {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("auth") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}

@Composable
fun HomeActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}



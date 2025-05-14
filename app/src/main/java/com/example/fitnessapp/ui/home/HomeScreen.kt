package com.example.fitnessapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth

// Ana ekranımız burada
@Composable
fun HomeScreen(navController: NavController) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Kullanıcı"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo veya profil resmi
        Image(
            painter = painterResource(id = R.drawable.logo), // logo.png veya profil resmi
            contentDescription = "Logo",
            modifier = Modifier
                .size(240.dp)
                .padding(top = 24.dp, bottom = 16.dp)
        )

        Text(
            text = "Hoş geldin",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        HomeActionButton("Profilini Güncelle") {
            navController.navigate("profile")
        }

        HomeActionButton("Yemek Listesi") {
            navController.navigate("foods")
        }

        HomeActionButton("Egzersiz Listesi") {
            navController.navigate("exercises")
        }

        HomeActionButton("Çıkış Yap") {
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
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}




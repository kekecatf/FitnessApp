package com.example.fitnessapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.fitnessapp.ui.theme.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.theme.FitnessAppTheme


@Composable
fun HomeScreen(navController: NavController,themeViewModel: ThemeViewModel) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Kullanıcı"
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()


    // Tüm ekran temaya göre sarılıyor
    FitnessAppTheme(darkTheme = isDarkTheme) {
        val backgroundColor = if (isDarkTheme) Color.Black else Color.White
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
                IconButton(
                    onClick = { themeViewModel.toggleTheme() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkTheme) R.drawable.sun else R.drawable.moon
                        ),
                        contentDescription = "Tema Değiştir",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                // Logo veya profil resmi
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(240.dp)
                        .padding(top = 24.dp, bottom = 16.dp)
                )

                Text(
                    text = "Hoş geldin",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontFamily = FontFamily.Serif, // veya customFont
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )


                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif, // veya customFont
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
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

                HomeActionButton("Notlarım") {
                    navController.navigate("notes")
                }

                HomeActionButton("Çıkış Yap") {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
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
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif

            ),
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}




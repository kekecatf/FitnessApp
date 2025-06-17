package com.example.fitnessapp.ui.home

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.google.firebase.auth.FirebaseAuth
import com.example.fitnessapp.ui.theme.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.auth.ProfileViewModel
import com.example.fitnessapp.ui.theme.FitnessAppTheme


@Composable
fun HomeScreen(navController: NavController, profileViewModel: ProfileViewModel = viewModel()) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Kullanıcı"
    val userName by profileViewModel.name.collectAsState()

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    // İlk açılışta yükleme işlemi başlat
    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

    // Tüm ekran temaya göre sarılıyor
    FitnessAppTheme(darkTheme = isDarkTheme) {
        val backgroundImageRes = if (isDarkTheme) {
            R.drawable.ana_sayfa_koyu
        } else {
            R.drawable.ana_sayfa_aydinlik
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = backgroundImageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {

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
                    text = userName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Serif, // veya customFont
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                HomeActionButton("Ayarlar") {
                    navController.navigate("settings")
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




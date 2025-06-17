package com.example.fitnessapp.ui.home

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val userName by profileViewModel.name.collectAsState()
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Kullanıcı"

    LaunchedEffect(Unit) {
        profileViewModel.loadUserProfile()
    }

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
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Üst kısım - logo ve kullanıcı ismi
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Butonlar
                HomeActionButton("📝 Notlarım") { navController.navigate("notes") }
                HomeActionButton("🍽️ Yemek Listesi") { navController.navigate("foods") }
                HomeActionButton("🏋️‍♂️ Egzersiz Listesi") { navController.navigate("exercises") }
                HomeActionButton("⚙️ Ayarlar") { navController.navigate("settings") }
                HomeActionButton("🚪 Çıkış Yap") {
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
            .padding(vertical = 14.dp)
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}






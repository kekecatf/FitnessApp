package com.example.fitnessapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.ui.auth.AuthScreen
import com.example.fitnessapp.ui.exercise.ExerciseScreen
import com.example.fitnessapp.ui.food.FoodSuggestionScreen
import com.example.fitnessapp.ui.home.HomeScreen
import com.example.fitnessapp.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth


// Sayfa gecislerini buradan ayarliyoruz
@Composable
fun MainNavigation() {
    val navController: NavHostController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "home" else "auth"
    ) {
        composable("auth") {
            AuthScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("profile") {
            ProfileScreen(navController)
        }
        composable("foods") {
            FoodSuggestionScreen(apiKey = "c93fd713252a40d3a193cea2caf2c195") // ← buraya kendi key'ini koy
        }
        composable("exercises") {
            ExerciseScreen()
        }

    }
}

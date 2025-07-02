package com.example.fitnessapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.ui.auth.AuthScreen
import com.example.fitnessapp.ui.auth.ProfileSetupScreen
import com.example.fitnessapp.ui.exercise.ExerciseDbScreen
import com.example.fitnessapp.ui.home.HomeScreen
import com.example.fitnessapp.ui.profile.ProfileScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.fitnessapp.ui.notes.NotesScreen
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.ui.food.FoodScreen
import com.example.fitnessapp.ui.home.SettingsScreen
import com.example.fitnessapp.ui.recommendation.RecommendationScreen


// Sayfa gecislerini buradan ayarliyoruz
@Composable
fun MainNavigation() {
    val navController: NavHostController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isLoggedIn = currentUser != null

    val themeViewModel: ThemeViewModel = viewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    FitnessAppTheme(darkTheme = isDarkTheme) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "home" else "auth"
        ) {
            composable("auth") {
                AuthScreen(navController)
            }

            composable("profile_setup"){
                ProfileSetupScreen(navController)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("profile") {
                ProfileScreen(navController)
            }
            composable("foods") {
                FoodScreen(navController)
            }

            composable("exercises") {
                ExerciseDbScreen()
            }
            composable("notes") {
                NotesScreen()
            }
            composable("recommendation") {
                RecommendationScreen()
            }
            composable("settings") {
                SettingsScreen(navController = navController)
            }

        }
    }
}

package com.example.fitnessapp.ui.recommendation

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.fitnessapp.data.api.ApiClient
import com.example.fitnessapp.data.api.NutritionixClient
import com.example.fitnessapp.data.model.FoodRequest
import com.example.fitnessapp.data.model.Nutrient
import com.example.fitnessapp.data.model.ExerciseDbItem
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel
import com.example.fitnessapp.util.GoalReminderManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun RecommendationScreen() {
    val context = LocalContext.current
    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val user = FirebaseAuth.getInstance().currentUser
    val firestore = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var workoutPlan by remember { mutableStateOf<List<String>>(emptyList()) }
    var dailyCalories by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var weight by remember { mutableStateOf(0) }
    var targetWeight by remember { mutableStateOf(0) }
    var trainingDays by remember { mutableStateOf(3) }
    var gender by remember { mutableStateOf("Erkek") }
    var showGoalDialog by remember { mutableStateOf(false) }

    var foodSuggestions by remember { mutableStateOf<List<Nutrient>>(emptyList()) }
    var recommendedExercises by remember { mutableStateOf<List<ExerciseDbItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            if (GoalReminderManager.shouldAskGoal()) {
                showGoalDialog = true
            }
        }

        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    height = doc.getLong("height")?.toInt() ?: 0
                    weight = doc.getLong("weight")?.toInt() ?: 0
                    targetWeight = doc.getLong("targetWeight")?.toInt() ?: 0
                    trainingDays = doc.getLong("trainingDays")?.toInt() ?: 3
                    gender = doc.getString("gender") ?: "Erkek"

                    workoutPlan = generateWorkoutPlan(trainingDays, weight, targetWeight)
                    dailyCalories = calculateCalories(weight, height, gender, trainingDays)

                    scope.launch {
                        foodSuggestions = try {
                            val goal = GoalReminderManager.getWeeklyGoal() ?: "fat_loss"
                            val input = if (goal == "fat_loss") "2 eggs, 1 toast, 1 banana" else "3 eggs, oats, banana"
                            val response = NutritionixClient.api.getNutrition(FoodRequest(input))
                            response.foods
                        } catch (e: Exception) {
                            emptyList()
                        }

                        recommendedExercises = try {
                            ApiClient.apiService
                                .getExercisesByTarget(if (targetWeight < weight) "cardiovascular system" else "pectorals")
                                .filter { it.gifUrl.isNotBlank() }
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }
                }
        }
    }

    FitnessAppTheme(darkTheme = isDarkTheme) {
        val backgroundColor = if (isDarkTheme) Color.Black else Color.White

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Günlük Planınız", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tahmini Kalori İhtiyacı: $dailyCalories kcal")
            Spacer(modifier = Modifier.height(16.dp))

            Text("Antrenman Planı:", style = MaterialTheme.typography.titleMedium)
            workoutPlan.forEach { workout ->
                Text("- $workout")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Beslenme Önerisi:", style = MaterialTheme.typography.titleMedium)
            foodSuggestions.forEach {
                Text("${it.food_name}: ${it.nf_calories} kcal | Protein: ${it.nf_protein}g")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Önerilen Egzersizler:", style = MaterialTheme.typography.titleMedium)
            recommendedExercises.take(3).forEach {
                Text("${it.name} - ${it.bodyPart}")
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = rememberAsyncImagePainter(it.gifUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (showGoalDialog) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Bu haftaki hedefin nedir?") },
                text = { Text("Yağ yakımı mı yoksa kas kazanımı mı hedefliyorsun?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            GoalReminderManager.saveGoal("fat_loss")
                            showGoalDialog = false
                        }
                    }) {
                        Text("Yağ Kaybı")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        scope.launch {
                            GoalReminderManager.saveGoal("muscle_gain")
                            showGoalDialog = false
                        }
                    }) {
                        Text("Kas Kazanımı")
                    }
                }
            )
        }
    }
}

fun generateWorkoutPlan(trainingDays: Int, weight: Int, targetWeight: Int): List<String> {
    val goal = if (targetWeight < weight) "fat loss" else "muscle gain"
    val workouts = when (goal) {
        "fat loss" -> listOf("Cardio", "Full Body", "HIIT", "Yoga")
        "muscle gain" -> listOf("Chest", "Back", "Legs", "Arms", "Shoulders")
        else -> listOf("General Fitness")
    }
    return workouts.take(trainingDays)
}

fun calculateCalories(weight: Int, height: Int, gender: String, activityLevel: Int): Int {
    val bmr = if (gender == "Kadın")
        655 + (9.6 * weight) + (1.8 * height) - (4.7 * 25)
    else
        66 + (13.7 * weight) + (5.0 * height) - (6.8 * 25)

    val activityFactor = when (activityLevel) {
        1 -> 1.2
        2 -> 1.3
        3 -> 1.375
        4 -> 1.45
        5 -> 1.55
        6 -> 1.7
        else -> 1.3
    }
    return (bmr * activityFactor).toInt()
}

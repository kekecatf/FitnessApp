package com.example.fitnessapp.ui.food

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel

@Composable
fun FoodScreen(navController: NavController, viewModel: FoodViewModel = viewModel()) {
    var query by remember { mutableStateOf("") }
    val foods by viewModel.nutrients.collectAsState()
    val error by viewModel.error.collectAsState()

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    // Tüm ekran temaya göre sarılıyor
    FitnessAppTheme(darkTheme = isDarkTheme) {

        val backgroundColor = if (isDarkTheme) Color.Black else Color.White

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Yemekleri girin (örn. 2 egg, 1 bread)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.fetchNutrition(query) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Besin Bilgisini Getir")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text("Hata: $error", color = MaterialTheme.colorScheme.error)
            }

            LazyColumn {
                items(foods) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Besin: ${item.food_name}")
                            Text("Kalori: ${item.nf_calories} kcal")
                            Text("Protein: ${item.nf_protein} g")
                            Text("Yağ: ${item.nf_total_fat} g")
                            Text("Karbonhidrat: ${item.nf_total_carbohydrate} g")
                        }
                    }
                }
            }
        }
    }
}
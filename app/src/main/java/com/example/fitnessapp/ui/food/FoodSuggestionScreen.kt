package com.example.fitnessapp.ui.food

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun FoodSuggestionScreen(
    apiKey: String,
    minCal: Int = 300,
    maxCal: Int = 600,
    viewModel: FoodViewModel = viewModel()
) {
    val foodList by viewModel.foods.collectAsState()

    val errorMessage by viewModel.errorMessage.collectAsState()

    errorMessage?.let {
        Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
    }

    LaunchedEffect(Unit) {
        viewModel.fetchFoodSuggestions(minCal, maxCal, apiKey)
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(foodList) { food ->
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)) {
                Row(modifier = Modifier.padding(8.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(food.image),
                        contentDescription = food.title,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(text = food.title, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

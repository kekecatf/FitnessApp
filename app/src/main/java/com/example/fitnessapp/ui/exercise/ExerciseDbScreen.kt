package com.example.fitnessapp.ui.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberImagePainter
import com.example.fitnessapp.data.model.ExerciseDbItem

@Composable
fun ExerciseDbScreen(viewModel: ExerciseDbViewModel = viewModel()) {
    val exercises by viewModel.exercises.collectAsState()

    val bodyParts = listOf("waist", "chest", "back")
    val equipmentTypes = listOf("body weight", "assisted", "cable", "leverage machine")
    val targets = listOf("abs", "lats", "pectorals")

    val errorMessage by viewModel.errorMessage.collectAsState()
    errorMessage?.let {
        Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
    }


    LaunchedEffect(Unit) {
        viewModel.fetchExercises()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bölge Seç", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bodyParts) {
                Button(onClick = { viewModel.setBodyPartFilter(it) }) {
                    Text(it.capitalize())
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("Ekipman Seç", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(equipmentTypes) {
                Button(onClick = { viewModel.setEquipmentFilter(it) }) {
                    Text(it.capitalize())
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text("Kas Grubu Seç", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(targets) {
                Button(onClick = { viewModel.setTargetFilter(it) }) {
                    Text(it.capitalize())
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(exercises) {
                ExerciseCard(it)
            }
        }
    }
}


@Composable
fun ExerciseCard(exercise: ExerciseDbItem) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Kas: ${exercise.target}")
            Text(text = "Vücut bölgesi: ${exercise.bodyPart}")
            Text(text = "Ekipman: ${exercise.equipment}")
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                painter = rememberImagePainter(exercise.gifUrl),
                contentDescription = null,
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

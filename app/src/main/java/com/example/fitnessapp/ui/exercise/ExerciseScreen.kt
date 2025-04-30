package com.example.fitnessapp.ui.exercise

import android.text.Html
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExerciseScreen(
    language: Int = 2,
    viewModel: ExerciseViewModel = viewModel()
) {
    val exercises by viewModel.exercises.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchExercises(language)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(exercises) { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val exerciseName = exercise.name ?: "İsimsiz Egzersiz"
                    val rawDescription = exercise.description
                    val cleanDescription = if (!rawDescription.isNullOrBlank()) {
                        Html.fromHtml(rawDescription, Html.FROM_HTML_MODE_COMPACT).toString()
                    } else {
                        "Açıklama yok"
                    }

                    Text(text = exerciseName, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = cleanDescription, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

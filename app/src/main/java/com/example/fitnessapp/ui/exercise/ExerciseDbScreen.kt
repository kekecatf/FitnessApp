package com.example.fitnessapp.ui.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.fitnessapp.data.model.ExerciseDbItem

// ExerciseDbScreen.kt
@Composable
fun ExerciseDbScreen(viewModel: ExerciseDbViewModel = viewModel()) {
    val exercises by viewModel.exercises.collectAsState()
    val bodyParts by viewModel.bodyParts.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedBodyPart by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchBodyParts()
        viewModel.fetchExercises()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        if (bodyParts.isNotEmpty()) {
            BodyPartDropdown(
                label = "Vücut Bölgesi",
                options = bodyParts,
                selectedOption = selectedBodyPart,
                onSelect = {
                    selectedBodyPart = it
                    viewModel.fetchExercises(it)
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                selectedBodyPart = null
                viewModel.fetchExercises()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Filtreyi Kaldır")
        }

        Spacer(modifier = Modifier.height(8.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn {
            items(exercises) { exercise ->
                ExerciseCard(exercise)
            }
        }
    }
}


@Composable
fun BodyPartDropdown(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label)
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedOption ?: "Tümü")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Tümü") },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun ExerciseCard(exercise: ExerciseDbItem) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Kas: ${exercise.target}")
            Text(text = "Bölge: ${exercise.bodyPart}")
            Text(text = "Ekipman: ${exercise.equipment}")
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(exercise.gifUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}


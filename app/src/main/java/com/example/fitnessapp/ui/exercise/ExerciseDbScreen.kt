package com.example.fitnessapp.ui.exercise

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.ExerciseDbItem
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel

// ExerciseDbScreen.kt
@Composable
fun ExerciseDbScreen(viewModel: ExerciseDbViewModel = viewModel()) {
    val exercises by viewModel.exercises.collectAsState()
    val bodyParts by viewModel.bodyParts.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val equipmentList by viewModel.equipmentList.collectAsState()
    val targetList by viewModel.targetList.collectAsState()

    var selectedEquipment by remember { mutableStateOf<String?>(null) }
    var selectedTarget by remember { mutableStateOf<String?>(null) }


    var selectedBodyPart by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchBodyParts()
        viewModel.fetchExercises()
        viewModel.fetchEquipmentList()
        viewModel.fetchTargetList()

    }

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

            if (bodyParts.isNotEmpty()) {
                // Equipment filtre
                if (equipmentList.isNotEmpty()) {
                    BodyPartDropdown(
                        label = "Ekipman",
                        options = equipmentList,
                        selectedOption = selectedEquipment,
                        onSelect = {
                            selectedEquipment = it
                            it?.let { viewModel.fetchExercisesByEquipment(it) }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // Target filtre
                if (targetList.isNotEmpty()) {
                    BodyPartDropdown(
                        label = "Hedef Kas",
                        options = targetList,
                        selectedOption = selectedTarget,
                        onSelect = {
                            selectedTarget = it
                            it?.let { viewModel.fetchExercisesByTarget(it) }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    selectedBodyPart = null
                    selectedEquipment = null
                    selectedTarget = null
                    viewModel.fetchExercises()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Filtreyi Kaldır")
            }


            Spacer(modifier = Modifier.height(8.dp))

            errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            if (exercises.isEmpty()) {
                Text(
                    text = "Seçilen filtrelere uygun egzersiz bulunamadı.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn {
                    items(exercises) { exercise ->
                        ExerciseCard(exercise)
                    }
                }
            }

            LazyColumn {
                items(exercises) { exercise ->
                    ExerciseCard(exercise)
                }
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
        Text(text = label,color = MaterialTheme.colorScheme.onBackground)
        OutlinedButton(onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        )) {
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


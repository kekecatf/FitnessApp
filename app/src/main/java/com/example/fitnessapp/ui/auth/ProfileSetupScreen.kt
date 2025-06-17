package com.example.fitnessapp.ui.auth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }

    var gender by remember { mutableStateOf("") }
    var genderExpanded by remember { mutableStateOf(false) }

    var trainingDays by remember { mutableStateOf("") }
    var trainingExpanded by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }

    val genderOptions = listOf("Erkek", "Kadın", "Diğer")
    val daysOptions = (1..7).map { it.toString() }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Soyad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = height,
                onValueChange = { if (it.all { char -> char.isDigit() }) height = it },
                label = { Text("Boy (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { if (it.all { char -> char.isDigit() }) weight = it },
                label = { Text("Kilo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = targetWeight,
                onValueChange = { if (it.all { char -> char.isDigit() }) targetWeight = it },
                label = { Text("Hedef Kilo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded }
            ) {
                TextField(
                    value = gender.ifEmpty { "Cinsiyet seçin" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cinsiyet") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { genderExpanded = true }
                )

                ExposedDropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                gender = option
                                genderExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = trainingExpanded,
                onExpandedChange = { trainingExpanded = !trainingExpanded }
            ) {
                TextField(
                    value = trainingDays.ifEmpty { "Haftada kaç gün" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Antreman Günü") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = trainingExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { trainingExpanded = true }
                )

                ExposedDropdownMenu(
                    expanded = trainingExpanded,
                    onDismissRequest = { trainingExpanded = false }
                ) {
                    daysOptions.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                trainingDays = day
                                trainingExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (
                        name.isBlank() ||
                        lastName.isBlank() ||
                        height.isBlank() ||
                        weight.isBlank() ||
                        targetWeight.isBlank() ||
                        gender.isBlank() ||
                        trainingDays.isBlank()
                    ) {
                        message = "Lütfen tüm alanları doldurun"
                        return@Button
                    }

                    try {
                        viewModel.saveProfile(
                            name = name,
                            lastName = lastName,
                            height = height.toInt(),
                            weight = weight.toInt(),
                            targetWeight = targetWeight.toInt(),
                            gender = gender,
                            trainingDays = trainingDays.toInt(),
                            onSuccess = {
                                navController.navigate("home") {
                                    popUpTo("profile_setup") { inclusive = true }
                                }
                            },
                            onError = { msg -> message = msg }
                        )
                    } catch (e: NumberFormatException) {
                        message = "Lütfen boy, kilo ve hedef kilo alanlarına geçerli sayılar girin."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kaydet ve Devam Et")
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
    }
}






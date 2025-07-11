package com.example.fitnessapp.ui.profile

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }
    var trainingDays by remember { mutableStateOf("") }
    var daysExpanded by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val firestore = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    // Mevcut kullanıcı verilerini çek
    LaunchedEffect(Unit) {
        user?.let {
            firestore.collection("users").document(it.uid).get()
                .addOnSuccessListener { doc ->
                    height = (doc.getLong("height")?.toString()) ?: ""
                    weight = (doc.getLong("weight")?.toString()) ?: ""
                    targetWeight = (doc.getLong("targetWeight")?.toString()) ?: ""
                    trainingDays = (doc.getLong("trainingDays")?.toString()) ?: ""
                }
        }
    }
// Tüm ekran temaya göre sarılıyor
    FitnessAppTheme(darkTheme = isDarkTheme) {
        val backgroundColor = if (isDarkTheme) Color.Black else Color.White
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
//            IconButton(
//                onClick = { themeViewModel.toggleTheme() },
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Icon(
//                    painter = painterResource(
//                        id = if (isDarkTheme) R.drawable.sun else R.drawable.moon
//                    ),
//                    contentDescription = "Tema Değiştir",
//                    tint = MaterialTheme.colorScheme.onBackground
//                )
//            }
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                label = { Text("Boy (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Kilo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = targetWeight,
                onValueChange = { targetWeight = it },
                label = { Text("Hedef Kilo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // Haftada kaç gün dropdown
            val daysOptions = (1..7).map { it.toString() }
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = trainingDays.ifEmpty { "Haftada kaç gün" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Antreman Günü") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable { expanded = true }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    daysOptions.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                trainingDays = day
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    // basit validasyon
                    if (height.isBlank() ||
                        weight.isBlank() ||
                        targetWeight.isBlank() ||
                        trainingDays.isBlank()
                    ) {
                        message = "Lütfen tüm alanları doldurun"
                        return@Button
                    }
                    user?.let {
                        val data = mapOf(
                            "height" to height.toIntOrNull(),
                            "weight" to weight.toIntOrNull(),
                            "targetWeight" to targetWeight.toIntOrNull(),
                            "trainingDays" to trainingDays.toIntOrNull()
                        )
                        firestore.collection("users")
                            .document(it.uid)
                            .update(data)
                            .addOnSuccessListener { message = "Profil güncellendi." }
                            .addOnFailureListener { e -> message = "Hata: ${e.message}" }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Kaydet")
            }

            Spacer(Modifier.height(16.dp))
            Text(text = message, color = MaterialTheme.colorScheme.error)
        }
    }
}

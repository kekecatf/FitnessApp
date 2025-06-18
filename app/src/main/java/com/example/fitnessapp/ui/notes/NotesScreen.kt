package com.example.fitnessapp.ui.notes

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitnessapp.data.projectfiles.NoteItem
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.theme.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.fitnessapp.data.projectfiles.NoteApiClient


@Composable
fun NotesScreen() {
    var todoText by remember { mutableStateOf("") }
    val todoItems = remember { mutableStateListOf<TodoItem>() }
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""
    var message by remember { mutableStateOf("") }

    val themeViewModel: ThemeViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    // Notları yükle (PHP'den çek)
    LaunchedEffect(uid) {
        if (uid.isNotBlank()) {
            try {
                val notes = NoteApiClient.api.getNotes(uid)
                todoItems.clear()
                notes.forEach {
                    todoItems.add(TodoItem(it.text, it.isDone))
                }
            } catch (e: Exception) {
                message = "Notlar alınamadı: ${e.message}"
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
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = todoText,
                    onValueChange = { todoText = it },
                    label = { Text("Yapılacaklar") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (todoText.isNotBlank()) {
                        val newItem = TodoItem(todoText, false)
                        todoItems.add(newItem)
                        todoText = ""

                        // API’ye gönder
                        coroutineScope.launch {
                            try {
                                val response = NoteApiClient.api.saveNote(
                                    NoteItem(uid = uid, text = newItem.text, isDone = newItem.isDone)
                                )
                                if (response.isSuccessful) {
                                    message = "Not kaydedildi"
                                } else {
                                    message = "Sunucu hatası: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                message = "Bağlantı hatası: ${e.message}"
                            }
                        }
                    }
                }) {
                    Text("Ekle")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            todoItems.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = item.isDone,
                        onCheckedChange = {
                            todoItems[index] = item.copy(isDone = it)
                        }
                    )
                    Text(
                        text = item.text,
                        style = if (item.isDone)
                            TextStyle(
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        else
                            TextStyle(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { todoItems.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (message.isNotBlank()) {
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

//@Composable
//fun NotesScreen() {
//    var todoText by remember { mutableStateOf("") }
//    val todoItems = remember { mutableStateListOf<TodoItem>() }
//    val user = FirebaseAuth.getInstance().currentUser
//    val firestore = FirebaseFirestore.getInstance()
//    var message by remember { mutableStateOf("") }
//
//    // Notları yükle
//    LaunchedEffect(Unit) {
//        user?.let {
//            firestore.collection("users").document(it.uid).get()
//                .addOnSuccessListener { doc ->
//                    val notes = doc.get("notes") as? List<Map<String, Any>>
//                    notes?.forEach {
//                        val text = it["text"] as? String ?: ""
//                        val isDone = it["isDone"] as? Boolean ?: false
//                        todoItems.add(TodoItem(text, isDone))
//                    }
//                }
//        }
//    }
//
//    val themeViewModel: ThemeViewModel = viewModel(
//        factory = ViewModelProvider.AndroidViewModelFactory(
//            LocalContext.current.applicationContext as Application
//        )
//    )
//    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
//
//
//    // Tüm ekran temaya göre sarılıyor
//    FitnessAppTheme(darkTheme = isDarkTheme) {
//
//        val backgroundColor = if (isDarkTheme) Color.Black else Color.White
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(backgroundColor)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(32.dp))
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                OutlinedTextField(
//                    value = todoText,
//                    onValueChange = { todoText = it },
//                    label = { Text("Yapılacaklar") },
//                    modifier = Modifier.weight(1f),
//                    keyboardOptions = KeyboardOptions.Default.copy(
//                        imeAction = ImeAction.Done,
//                        keyboardType = KeyboardType.Text
//                    )
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Button(onClick = {
//                    if (todoText.isNotBlank()) {
//                        todoItems.add(TodoItem(todoText, false))
//                        todoText = ""
//                    }
//                }) {
//                    Text("Ekle")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            todoItems.forEachIndexed { index, item ->
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
//                ) {
//                    Checkbox(
//                        checked = item.isDone,
//                        onCheckedChange = {
//                            todoItems[index] = item.copy(isDone = it)
//                        }
//                    )
//                    Text(
//                        text = item.text,
//                        style = if (item.isDone)
//                            TextStyle(
//                                textDecoration = TextDecoration.LineThrough,
//                                color = MaterialTheme.colorScheme.onBackground
//                            )
//                        else
//                            TextStyle(
//                                color = MaterialTheme.colorScheme.onBackground
//                            ),
//                        modifier = Modifier.weight(1f)
//                    )
//                    IconButton(onClick = { todoItems.removeAt(index) }) {
//                        Icon(Icons.Default.Delete, contentDescription = "Sil",tint = MaterialTheme.colorScheme.onBackground)
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = {
//                    user?.let {
//                        val notesData = todoItems.map { note ->
//                            mapOf("text" to note.text, "isDone" to note.isDone)
//                        }
//                        firestore.collection("users").document(it.uid)
//                            .update("notes", notesData)
//                            .addOnSuccessListener {
//                                message = "Notlar kaydedildi."
//                            }
//                            .addOnFailureListener { e ->
//                                message = "Hata: ${e.message}"
//                            }
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Kaydet")
//            }
//
//            if (message.isNotBlank()) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(text = message, color = MaterialTheme.colorScheme.primary)
//            }
//        }
//    }
//}

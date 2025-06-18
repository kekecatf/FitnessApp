package com.example.fitnessapp.ui.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessapp.data.room.AppDatabase
import com.example.fitnessapp.data.room.TodoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).todoDao()

    private val _notes = MutableStateFlow<List<TodoEntity>>(emptyList())
    val notes: StateFlow<List<TodoEntity>> = _notes

    fun loadNotes() {
        viewModelScope.launch {
            _notes.value = dao.getAllNotes()
        }
    }

    fun addNote(text: String) {
        viewModelScope.launch {
            dao.insertNote(TodoEntity(text = text, isDone = false))
            loadNotes()
        }
    }

    fun toggleNote(note: TodoEntity) {
        viewModelScope.launch {
            dao.updateNote(note.copy(isDone = !note.isDone))
            loadNotes()
        }
    }

    fun deleteNote(note: TodoEntity) {
        viewModelScope.launch {
            dao.deleteNote(note)
            loadNotes()
        }
    }
}

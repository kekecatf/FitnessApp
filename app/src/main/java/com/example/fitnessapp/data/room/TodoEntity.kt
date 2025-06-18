package com.example.fitnessapp.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val isDone: Boolean
)

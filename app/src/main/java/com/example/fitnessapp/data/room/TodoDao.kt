package com.example.fitnessapp.data.room

import androidx.room.*

@Dao
interface TodoDao {
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<TodoEntity>

    @Insert
    suspend fun insertNote(note: TodoEntity)

    @Update
    suspend fun updateNote(note: TodoEntity)

    @Delete
    suspend fun deleteNote(note: TodoEntity)
}

package com.example.fitnessapp.data.sqlveritabani

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NoteApiService {
    @POST("save_note.php")
    suspend fun saveNote(@Body note: NoteItem): Response<Unit>

    @GET("get_notes.php")
    suspend fun getNotes(@Query("uid") uid: String): List<NoteItem>
}

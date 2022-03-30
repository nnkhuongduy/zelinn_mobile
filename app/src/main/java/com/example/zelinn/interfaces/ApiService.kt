package com.example.zelinn.interfaces

import com.example.zelinn.classes.BoardModel
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/boards")
    fun getBoards(): Call<List<BoardModel>>
}
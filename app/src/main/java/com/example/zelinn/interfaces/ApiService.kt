package com.example.zelinn.interfaces

import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.PostLoginBody
import com.example.zelinn.classes.PostVerifyBody
import com.example.zelinn.classes.UserModel
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/boards")
    fun getBoards(): Call<List<BoardModel>>

    @POST("/users/login")
    suspend fun login(@Body requestBody: PostLoginBody): Response<Void>

    @POST("/users/verify")
    suspend fun verify(@Body requestBody: PostVerifyBody): Response<UserModel>
}
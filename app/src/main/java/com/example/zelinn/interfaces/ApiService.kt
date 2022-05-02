package com.example.zelinn.interfaces

import com.example.zelinn.classes.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/boards")
    fun getBoards(): Call<List<BoardModel>>

    @POST("/boards")
    fun postBoard(@Body requestBody: PostBoardBody): Call<Void>

    @POST("/boards/update")
    fun postUpdateBoard(@Body requestBody: PostUpdateBoardBody): Call<BoardModel>

    @POST("/auth/login")
    suspend fun login(@Body requestBody: PostLoginBody): Response<Void>

    @POST("/auth/verify")
    fun verifyUser(@Body requestBody: PostVerifyBody): Call<PostVerifyResponse>

    @GET("/auth")
    fun auth(): Call<UserModel>

    @POST("/users/register")
    suspend fun register(@Body requestBody: PostRegisterBody): Response<Void>

    @POST("/users/edit")
    fun editUser(@Body requestBody: PostEditUserBody): Call<UserModel>

    @Multipart
    @POST("/upload/avatar")
    fun uploadAvatarUser(@Part part: MultipartBody.Part): Call<UserModel>

    @Multipart
    @POST("/upload/thumbnail")
    fun uploadBoardThumbnail(@Part part: MultipartBody.Part): Call<UploadBoardThumbnailResponse>
}
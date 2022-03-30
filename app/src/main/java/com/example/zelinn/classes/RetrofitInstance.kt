package com.example.zelinn.classes

import com.example.zelinn.interfaces.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        val retrofit: ApiService by lazy {
            val httpClient = OkHttpClient.Builder()
            val builder = Retrofit.Builder()
                .baseUrl("http://13.213.9.57/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
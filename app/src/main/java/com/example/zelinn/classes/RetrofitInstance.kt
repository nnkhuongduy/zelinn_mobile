package com.example.zelinn.classes

import com.example.zelinn.interfaces.ApiService
import com.orhanobut.hawk.Hawk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        val retrofit: ApiService by lazy {
            val httpClient = OkHttpClient.Builder().addInterceptor {
                return@addInterceptor it.proceed(it.request().newBuilder().addHeader("Authorization", "Bearer ${Hawk.get<String>("jwt")}").build())
            }
            val builder = Retrofit.Builder()
                .baseUrl("http://192.168.1.7:3000/")
//                .baseUrl("http://13.213.9.57/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
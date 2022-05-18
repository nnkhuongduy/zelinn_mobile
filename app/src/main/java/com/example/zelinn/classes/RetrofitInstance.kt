package com.example.zelinn.classes

import android.provider.Settings.Global.getString
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.interfaces.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class RetrofitInstance {
    companion object {
        val retrofit: ApiService by lazy {
            val httpClient = OkHttpClient.Builder().addInterceptor {
                return@addInterceptor it.proceed(
                    it.request().newBuilder().addHeader(
                        "Authorization",
                        "Bearer ${ZelinnApp.prefs.pull("com.example.zelinn.JWT", "")}"
                    ).build()
                )
            }
            val builder = Retrofit.Builder()
                .baseUrl("https://zelinn.pw/")
//                .baseUrl("http://192.168.1.9:3000/")
                .addConverterFactory(GsonConverterFactory.create())
            val retrofit = builder
                .client(httpClient.build())
                .build()
            retrofit.create(ApiService::class.java)
        }
    }
}
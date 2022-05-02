package com.example.zelinn

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.ActivityAuthBinding
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
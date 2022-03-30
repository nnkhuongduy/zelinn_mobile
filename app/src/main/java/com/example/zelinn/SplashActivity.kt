package com.example.zelinn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<Button>(R.id.splash_login)

        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }
    }
}
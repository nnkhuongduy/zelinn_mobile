package com.example.zelinn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.raycoarana.codeinputview.CodeInputView
import com.raycoarana.codeinputview.OnDigitInputListener

class VerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        val backBtn = findViewById<TextView>(R.id.login_verification_back)
        val confirmBtn = findViewById<TextView>(R.id.login_verification_action)
        val input = findViewById<CodeInputView>(R.id.login_verification_input)

        confirmBtn.isClickable = false

        confirmBtn.setOnClickListener {
            toHomepage()
        }
        backBtn.setOnClickListener {
            onBackPressed()
        }
        input.addOnCompleteListener {
            confirmBtn.isClickable = true
            confirmBtn.setBackgroundColor(
                ContextCompat.getColor(this, R.color.primary)
            )
        }

        input.addOnDigitInputListener(object: OnDigitInputListener {
            override fun onInput(inputDigit: Char) {
            }
            override fun onDelete() {
                confirmBtn.isClickable = false
                confirmBtn.setBackgroundColor(
                    ContextCompat.getColor(this@VerificationActivity, R.color.background_light)
                )
            }
        })
    }

    private fun toRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)

        startActivity(intent)
        finish()
    }

    private fun toHomepage() {
        val intent = Intent(this, HomeActivity::class.java)

        startActivity(intent)
        finish()
    }
}
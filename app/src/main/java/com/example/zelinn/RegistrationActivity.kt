package com.example.zelinn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val backBtn = findViewById<TextView>(R.id.registration_back)
        val actionBtn = findViewById<Button>(R.id.registration_action)

        backBtn.setOnClickListener {
            onBackPressed()
        }
        actionBtn.setOnClickListener {
            createDialog()
        }
    }

    private fun createDialog() {
        val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_registration_success)
        val dialog = builder.show()

        dialog.setOnDismissListener {
            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent)
            finish()
        }
    }
}
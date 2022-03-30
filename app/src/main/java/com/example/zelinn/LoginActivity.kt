package com.example.zelinn

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    private var _valid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val backBtn = findViewById<TextView>(R.id.login_back)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val input = findViewById<TextInputLayout>(R.id.login_input)

        backBtn.setOnClickListener {
            onBackPressed()
        }
        loginBtn.setOnClickListener {
            if (this._valid)
                createDialog()
        }
        input.editText?.doOnTextChanged { text, start, before, count ->
            val pattern = Patterns.EMAIL_ADDRESS

            this._valid = pattern.matcher(text).matches()

            loginBtn.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    if (this._valid) R.color.primary else R.color.background_light
                )
            )
            loginBtn.isClickable = this._valid
        }
    }

    private fun createDialog() {
        val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_login_mail)
        val dialog = builder.show()
        val button = dialog.findViewById<Button>(R.id.login_mail_dialog_action)

        button?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            val intent = Intent(this, VerificationActivity::class.java)

            startActivity(intent)
        }
    }
}
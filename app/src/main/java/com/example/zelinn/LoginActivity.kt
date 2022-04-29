package com.example.zelinn

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.zelinn.classes.PostLoginBody
import com.example.zelinn.classes.PostVerifyBody
import com.example.zelinn.classes.RetrofitInstance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private var _valid = false
    private lateinit var loginBtn: Button
    private lateinit var input: TextInputLayout
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val backBtn = findViewById<Button>(R.id.login_back)
        loginBtn = findViewById(R.id.login_btn)
        input = findViewById(R.id.login_input)
        spinner = findViewById(R.id.login_progress_spinner)

        backBtn.setOnClickListener {
            onBackPressed()
        }
        loginBtn.setOnClickListener {
            if (this._valid) {
                postVerify(getEmail());
            }
        }
        input.editText?.doOnTextChanged { text, start, before, count ->
            val pattern = Patterns.EMAIL_ADDRESS

            this._valid = pattern.matcher(text).matches()

            loginBtn.isEnabled = this._valid
        }
    }

    private fun getEmail(): String {
        return input.editText?.text.toString();
    }

    private fun createSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_login_mail)
        val dialog = builder.show()
        val button = dialog.findViewById<Button>(R.id.login_mail_dialog_action)

        button?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            val intent = Intent(this, VerificationActivity::class.java)
            intent.putExtra("email", getEmail())

            startActivity(intent)
        }
    }

    private fun createFailDialog() {
        val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_login_failed)
        builder.show()
    }

    private fun onLogging() {
        loginBtn.isEnabled = false
        loginBtn.text = ""
        spinner.visibility = View.VISIBLE
        input.isEnabled = false
    }

    private fun onLoginFinish() {
        loginBtn.isEnabled = true
        loginBtn.text = resources.getText(R.string.common_continue)
        spinner.visibility = View.GONE
        input.isEnabled = true
    }

    private fun postVerify(email: String) {
        val body = PostLoginBody(email)
        onLogging()

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.retrofit.login(body)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    createSuccessDialog()
                } else {
                    createFailDialog()
                }

                onLoginFinish()
            }
        }
    }
}
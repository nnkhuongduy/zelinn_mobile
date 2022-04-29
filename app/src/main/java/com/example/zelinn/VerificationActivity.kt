package com.example.zelinn

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.zelinn.classes.PostVerifyBody
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.raycoarana.codeinputview.CodeInputView
import com.raycoarana.codeinputview.OnDigitInputListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerificationActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var input: CodeInputView
    private lateinit var confirmBtn: Button
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        email = intent.getStringExtra("email")!!
        val backBtn = findViewById<Button>(R.id.login_verification_back)
        confirmBtn = findViewById(R.id.login_verification_action)
        input = findViewById(R.id.login_verification_input)
        spinner = findViewById(R.id.login_verification_progress_bar)

        confirmBtn.setOnClickListener {
            verifyCode()
        }
        backBtn.setOnClickListener {
            onBackPressed()
        }
        input.addOnCompleteListener {
            confirmBtn.isEnabled = true
        }

        input.addOnDigitInputListener(object : OnDigitInputListener {
            override fun onInput(inputDigit: Char) {
            }

            override fun onDelete() {
                confirmBtn.isEnabled = false
            }
        })
    }

    private fun onVerifying() {
        confirmBtn.isEnabled = false
        confirmBtn.text = ""
        spinner.visibility = View.VISIBLE
        input.isEnabled = false
    }

    private fun onVerifyFinish() {
        confirmBtn.isEnabled = true
        confirmBtn.text = resources.getText(R.string.common_continue)
        spinner.visibility = View.GONE
        input.isEnabled = true
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

    private fun showErrorDialog(code: Int) {
        runOnUiThread {
            onVerifyFinish()
            val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_error)
            val dialog = builder.show()

            val title = dialog.findViewById<TextView>(R.id.dialog_error_title)
            val subtitle = dialog.findViewById<TextView>(R.id.dialog_error_subtitle)
            subtitle?.visibility = View.GONE

            when (code) {
                410 -> title?.text = "Mã xác thực đã hết hạn!"
                404 -> title?.text = "Không tìm thấy người dùng này!"
                409 -> title?.text = "Mã xác thực không chính xác!"
                else -> title?.text = "Đã xảy ra lỗi trong quá trình xác thực!"
            }

            dialog.setOnDismissListener {
                onBackPressed()
            }
        }
    }

    private fun saveUser(user: UserModel) {
        val pref = this.getSharedPreferences(getString(R.string.preference_current_user), Context.MODE_PRIVATE)

        with(pref.edit()) {
            putString("id", user.id)
            putString("name", user.name)
            putString("email", user.email)
            putString("phone", user.phone)
            putString("birth", user.birth.toString())
        }
    }

    private fun verifyCode() {
        val body = PostVerifyBody(email, input.code)
        onVerifying()

        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitInstance.retrofit.verify(body)

            withContext(Dispatchers.Main) {
                onVerifyFinish()
                if (response.isSuccessful) {
                    val user = response.body()

                    if (user != null) {
                        saveUser(user)
                        if (user.name.isNullOrEmpty() || user.phone.isNullOrEmpty()) {
                            toRegistration()
                            return@withContext
                        }

                        toHomepage()
                    }
                }

                showErrorDialog(response.raw().code())
            }
        }
    }
}
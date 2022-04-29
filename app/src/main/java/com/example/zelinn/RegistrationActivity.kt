package com.example.zelinn

import java.util.Calendar
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog

class RegistrationActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var birthInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val backBtn = findViewById<TextView>(R.id.registration_back)
        val actionBtn = findViewById<Button>(R.id.registration_action)
        birthInput = findViewById(R.id.registration_birth_text)

        populate()

        birthInput.keyListener = null
        birthInput.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val now = Calendar.getInstance()
                    val dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.setOkText(getString(R.string.select))
                    dpd.setCancelText(getString(R.string.back))
                    dpd.maxDate = now
                    dpd.show(supportFragmentManager, "Datepickerdialog")
                }
            }

            return@setOnTouchListener false
        }

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

    private fun populate() {
        val pref = this.getSharedPreferences(getString(R.string.preference_current_user), Context.MODE_PRIVATE)
        val emailInput = findViewById<EditText>(R.id.registration_email_text)
        val nameInput = findViewById<EditText>(R.id.registration_name_text)
        val phoneInput = findViewById<EditText>(R.id.registration_phone_text)

        emailInput.setText(pref.getString("email", ""))
        nameInput.setText(pref.getString("name", ""))
        phoneInput.setText(pref.getString("phone", ""))
        birthInput.setText(pref.getString("date", ""))
    }


    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = "$dayOfMonth/$monthOfYear/$year"
        birthInput.setText(date)
    }
}
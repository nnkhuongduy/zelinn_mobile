package com.example.zelinn.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.zelinn.R
import com.example.zelinn.interfaces.PostLoginBody
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private var _valid = false
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var loginBtn: Button
    private lateinit var input: EditText
    private lateinit var spinner: ProgressBar


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val backBtn = root.findViewById<Button>(R.id.login_back)
        loginBtn = root.findViewById(R.id.login_btn)
        input = root.findViewById(R.id.login_input_text)
        spinner = root.findViewById(R.id.login_progress_spinner)

        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        loginBtn.setOnClickListener {
            postVerify()
        }
        input.doOnTextChanged { text, start, before, count ->
            val pattern = Patterns.EMAIL_ADDRESS

            this._valid = pattern.matcher(text).matches()

            loginBtn.isEnabled = this._valid
        }
        input.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                postVerify()
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getEmail(): String {
        return input.text.toString();
    }

    private fun createSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)
        val actionBtn = dialog.findViewById<Button>(R.id.dialog_success_action)!!
        val imgView = dialog.findViewById<ImageView>(R.id.dialog_success_img2)!!

        titleView?.text = getString(R.string.login_mail_success_dialog_title)
        actionBtn.visibility = View.VISIBLE
        actionBtn.text = getString(R.string.login_mail_dialog_action)
        imgView.setImageResource(R.drawable.ic_baseline_mail_outline_24)

        actionBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            findNavController().navigate(R.id.action_loginFragment_to_verificationFragment)
        }
    }

    private fun createFailDialog(code: Int) {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!
        val subtitle = dialog.findViewById<TextView>(R.id.dialog_error_subtitle)!!

        when (code) {
            503 -> {
                title.text = getString(R.string.login_limited_title)
                subtitle.text = getString(R.string.login_limited_subtitle)
                subtitle.visibility = View.VISIBLE
            }
            else -> title.text = getString(R.string.login_failed);
        }
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

    private fun postVerify() {
        if (this._valid) {
            val email = getEmail()
            val body = PostLoginBody(email)
            onLogging()

            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitInstance.retrofit.login(body)

                withContext(Dispatchers.Main) {
                    onLoginFinish()
                    viewModel.setEmail(getEmail())
                    if (response.isSuccessful) {
                        createSuccessDialog()
                    } else {
                        if (response.code() == 404)
                            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)

                        else createFailDialog(response.code())
                    }
                }
            }
        }
    }
}
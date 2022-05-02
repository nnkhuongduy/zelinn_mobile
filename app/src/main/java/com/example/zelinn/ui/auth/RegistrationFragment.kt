package com.example.zelinn.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.text.trimmedLength
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.zelinn.R
import com.example.zelinn.interfaces.PostRegisterBody
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentRegistrationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrationFragment: Fragment() {
    private var _binding: FragmentRegistrationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var actionBtn: Button
    private lateinit var progressCircle: ProgressBar
    private lateinit var email: String
    private lateinit var emailInput: EditText

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val backBtn = root.findViewById<TextView>(R.id.registration_back)
        emailInput = root.findViewById(R.id.registration_email_text)
        actionBtn = root.findViewById(R.id.registration_action)
        nameInput = root.findViewById(R.id.registration_name_text)
        phoneInput = root.findViewById(R.id.registration_phone_text)
        progressCircle = root.findViewById(R.id.registration_progress_circle)

        nameInput.addTextChangedListener {
            actionBtn.isEnabled = checkValid()
        }
        phoneInput.addTextChangedListener {
            actionBtn.isEnabled = checkValid()
        }
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        actionBtn.setOnClickListener {
            postRegister()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.email.observe(viewLifecycleOwner) {
            emailInput.setText(it)
            email = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_success_title)!!
        val subtitle = dialog.findViewById<TextView>(R.id.dialog_success_subtitle)!!
        val btn = dialog.findViewById<Button>(R.id.dialog_success_action)!!

        title.text = getString(R.string.registration_success)
        subtitle.text = getString(R.string.login_mail_success_dialog_title)
        btn.text = getString(R.string.login_mail_dialog_action)
        btn.visibility = View.VISIBLE
        subtitle.visibility = View.VISIBLE

        btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            findNavController().navigate(R.id.action_registrationFragment_to_verificationFragment)
        }
    }

    private fun checkValid(): Boolean {
        val name = nameInput.text.toString()
        val phone = phoneInput.text.toString()

        return name.trimmedLength() > 0 && phone.trimmedLength() >= 10 && phone.trimmedLength() < 13
    }

    private fun onRegistering() {
        progressCircle.visibility = View.VISIBLE
        actionBtn.isEnabled = false
        actionBtn.text = ""
        nameInput.isEnabled = false
        phoneInput.isEnabled = false
    }

    private fun onFinishRegister() {
        progressCircle.visibility = View.GONE
        actionBtn.isEnabled = true
        actionBtn.text = getString(R.string.common_continue)
        nameInput.isEnabled = true
        phoneInput.isEnabled = true
    }

    private fun showErrorDialog(code: Int) {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)

        title?.text = if(code == 406) getString(R.string.registration_existed) else getString(R.string.registration_failed)
    }

    private fun postRegister() {
        if (checkValid()) {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()
            val body = PostRegisterBody(email, name, phone)

            onRegistering()

            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitInstance.retrofit.register(body)

                withContext(Dispatchers.Main) {
                    onFinishRegister()
                    if (response.isSuccessful) {
                        showSuccessDialog()
                    } else {
                        showErrorDialog(response.code())
                    }
                }
            }
        }
    }
}
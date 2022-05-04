package com.example.zelinn.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.interfaces.PostVerifyBody
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentVerificationBinding
import com.example.zelinn.interfaces.PostVerifyResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import com.raycoarana.codeinputview.CodeInputView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationFragment: Fragment() {
    private var _binding: FragmentVerificationBinding? = null
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var email: String
    private lateinit var input: CodeInputView
    private lateinit var confirmBtn: Button
    private lateinit var spinner: ProgressBar

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val backBtn = root.findViewById<Button>(R.id.login_verification_back)
        confirmBtn = root.findViewById(R.id.login_verification_action)
        input = root.findViewById(R.id.login_verification_input)
        spinner = root.findViewById(R.id.login_verification_progress_bar)

        confirmBtn.setOnClickListener {
            postVerify()
        }
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        input.addOnCompleteListener {
            confirmBtn.isEnabled = true
            postVerify()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.email.observe(viewLifecycleOwner) {
            email = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun toHomepage() {
        val activity = requireActivity()
        val intent = Intent(activity, HomeActivity::class.java)

        startActivity(intent)
        activity.finish()
    }

    private fun showErrorDialog(code: Int) {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        when (code) {
            410 -> title.text = getString(R.string.verification_expired)
            409 -> title.text = getString(R.string.verification_incorrect)
            else -> title.text = getString(R.string.verification_failed)
        }

        dialog.setOnDismissListener {
            findNavController().popBackStack()
        }
    }

    private fun getAuth() {
        RetrofitInstance.retrofit.auth().enqueue(object: Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    Hawk.put(getString(R.string.preference_current_user), body)
                    toHomepage()
                }

                onVerifyFinish()
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                onVerifyFinish()
            }

        })
    }

    private fun postVerify() {
        val body = PostVerifyBody(email, input.code)
        onVerifying()

        RetrofitInstance.retrofit.verifyUser(body).enqueue(object: Callback<PostVerifyResponse> {
            override fun onResponse(call: Call<PostVerifyResponse>, response: Response<PostVerifyResponse>) {
                val body = response.body()

                if (response.isSuccessful && body != null) {
                    Hawk.put(getString(R.string.preference_jwt), body.token)
                    getAuth()
                    return
                }

                onVerifyFinish()
                showErrorDialog(response.code())
            }

            override fun onFailure(call: Call<PostVerifyResponse>, t: Throwable) {
                onVerifyFinish()
                showErrorDialog(500)
            }
        })

//        CoroutineScope(Dispatchers.IO).launch {
//            val response = RetrofitInstance.retrofit.verifyUser(body)
//
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    val jwt = response.body()
//
//                    if (!jwt.isNullOrEmpty()) {
//                        Hawk.put("jwt", jwt)
//                        getAuth()
//                        return@withContext
//                    }
//                }
//
//                onVerifyFinish()
//                showErrorDialog(response.code())
//            }
//        }
    }
}
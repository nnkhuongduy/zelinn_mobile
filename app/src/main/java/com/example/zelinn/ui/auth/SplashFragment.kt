package com.example.zelinn.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentSplashBinding
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashFragment: Fragment() {
    private var _binding: FragmentSplashBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val loginBtn = root.findViewById<Button>(R.id.splash_login)

        loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val jwt = ZelinnApp.prefs.pull(getString(R.string.preference_jwt), "")

        if (!jwt.isNullOrEmpty()) {
            RetrofitInstance.retrofit.auth().enqueue(object: Callback<UserModel> {
                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                    val user = response.body()

                    if (response.isSuccessful && user != null) {
                        ZelinnApp.prefs.push(getString(R.string.preference_current_user), Gson().toJson(user))

                        val activity = requireActivity()
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    } else showLoginLayout()
                }

                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                    showLoginLayout()
                }
            })
        } else showLoginLayout()
//        val jwt = ZelinnApp.prefs.pull(getString(R.string.preference_jwt), "")
//
//        if (!jwt.isNullOrEmpty()) {
//            RetrofitInstance.retrofit.auth().enqueue(object: Callback<UserModel> {
//                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
//                    val user = response.body()
//
//                    if (response.isSuccessful && user != null) {
//                        ZelinnApp.prefs.push(getString(R.string.preference_current_user), user)
//
//                        val activity = requireActivity()
//                        val intent = Intent(requireActivity(), HomeActivity::class.java)
//                        activity.startActivity(intent)
//                        activity.finish()
//                    } else showLoginLayout()
//                }
//
//                override fun onFailure(call: Call<UserModel>, t: Throwable) {
//                    showLoginLayout()
//                }
//            })
//        } else showLoginLayout()
//        showLoginLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoginLayout() {
        val layout = binding.root.findViewById<RelativeLayout>(R.id.splash_layout)

        layout.visibility = View.VISIBLE
    }
}
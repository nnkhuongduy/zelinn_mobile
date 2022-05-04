package com.example.zelinn.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zelinn.AuthActivity
import com.example.zelinn.R
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentProfileBinding
import com.orhanobut.hawk.Hawk
import io.ak1.BubbleTabBar

class ProfileFragment: Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private lateinit var imageView: ImageView
    private lateinit var usernameView: TextView
    private lateinit var emailView: TextView
    private lateinit var editBtn: Button
    private lateinit var infoBtn: Button
    private lateinit var logoutBtn: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        imageView = root.findViewById(R.id.profile_avatar_view)
        usernameView = root.findViewById(R.id.profile_username)
        emailView = root.findViewById(R.id.profile_email)
        editBtn = root.findViewById(R.id.profile_edit_btn)
        infoBtn = root.findViewById(R.id.profile_info_btn)
        logoutBtn = root.findViewById(R.id.profile_logout_btn)

        populate()
        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_profile, false)

        logoutBtn.setOnClickListener {
            logout()
        }
        editBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_profileEditFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populate() {
        val user = Hawk.get<UserModel>(getString(R.string.preference_current_user))

        if (user.avatar.isNullOrBlank())
            imageView.setImageResource(R.drawable.ic_person)
        else Glide
            .with(binding.root)
            .load(user.avatar)
            .into(imageView)

        usernameView.text = user.name
        emailView.text = user.email

    }

    private fun logout() {
        Hawk.delete(getString(R.string.preference_jwt))
        Hawk.delete(getString(R.string.preference_current_user))

        val activity = requireActivity()
        val intent = Intent(activity, AuthActivity::class.java)

        activity.startActivity(intent)
        activity.finish()
    }
}
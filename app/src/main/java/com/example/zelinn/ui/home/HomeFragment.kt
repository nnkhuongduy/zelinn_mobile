package com.example.zelinn.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentHomeBinding
import com.google.gson.Gson
import io.ak1.BubbleTabBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_dashboard, false)

        ZelinnApp.prefs.push("key", "encrypted value")

        val textView: TextView = binding.textView8
        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
        textView.text = "${getString(R.string.homepage_gretting)} ${if (user.name.length > 10) user.name.substring(0, 10) + "..." else user.name} \uD83D\uDC4B"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
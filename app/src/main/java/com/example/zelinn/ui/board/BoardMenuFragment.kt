package com.example.zelinn.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentBoardMenuBinding

class BoardMenuFragment : Fragment() {
    private var _binding: FragmentBoardMenuBinding? = null
    private lateinit var currentTag: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeFragment()
        }
    }
    private val model: BoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardMenuBinding.inflate(inflater, container, false)
        val root = binding.root

        val closeBtn = root.findViewById<Button>(R.id.board_menu_close)
        val configBtn = root.findViewById<Button>(R.id.board_menu_config_btn)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        configBtn.setOnClickListener {
            val fragment = BoardMenuConfigFragment()
            createFragment(fragment, "BOARD_MENU_CONFIG")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createFragment(fragment: Fragment, tag: String) {
        val activity = requireActivity()

        currentTag = tag
        model.resetUpdateBoard()

        activity.onBackPressedDispatcher.addCallback(backPressedCallback)
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun closeFragment() {
        val activity = requireActivity()

        backPressedCallback.remove()
        activity.supportFragmentManager.popBackStack(
            currentTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }
}
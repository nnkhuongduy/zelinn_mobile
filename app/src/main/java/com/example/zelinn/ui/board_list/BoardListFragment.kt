package com.example.zelinn.ui.board_list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.adapters.BoardAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentBoardListBinding
import com.example.zelinn.ui.home.HomeViewModel
import io.ak1.BubbleTabBar

class BoardListFragment : Fragment() {
    private lateinit var backdrop: FrameLayout
    private lateinit var backPressedCallback: OnBackPressedCallback

    private var _binding: FragmentBoardListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val backBtn = root.findViewById<TextView>(R.id.board_list_back_btn)
        val createBtn = root.findViewById<Button>(R.id.board_list_create_btn)
        backdrop = root.findViewById(R.id.board_list_backdrop)
        backPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeBoardCreateFragment()
            }
        }

        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_dashboard, false)

        backBtn.setOnClickListener {
            findNavController(this).popBackStack()
        }
        createBtn.setOnClickListener {
            createBoardCreateFragment()
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = binding.root.findViewById<RecyclerView>(R.id.board_list_rv)
        val adapter = BoardAdapter()

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        model.boards.observe(viewLifecycleOwner) {
            adapter.apply {
                submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createBoardCreateFragment() {
        val createBoardF = CreateBoardFragment()
        val activity = requireActivity() as HomeActivity

        backdrop.visibility = View.VISIBLE
        activity.setBottomNavVisibility(View.INVISIBLE)
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
        model.setBoard(null, "", getString(R.string.board_public_value))

        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_list_create_board_fragment, createBoardF, "CREATE_BOARD")
            .addToBackStack("CREATE_BOARD")
            .commit()
    }

    private fun closeBoardCreateFragment() {
        val activity = requireActivity() as HomeActivity

        activity.supportFragmentManager.popBackStack("CREATE_BOARD", FragmentManager.POP_BACK_STACK_INCLUSIVE)

        if (activity.supportFragmentManager.backStackEntryCount == 1) {
            backdrop.visibility = View.GONE
            backPressedCallback.remove()
            activity.setBottomNavVisibility(View.VISIBLE)
        }
    }
}
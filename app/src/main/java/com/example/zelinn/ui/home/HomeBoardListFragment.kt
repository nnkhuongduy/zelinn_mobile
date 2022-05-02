package com.example.zelinn.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.ui.board_list.BoardListFragment
import com.example.zelinn.R
import com.example.zelinn.adapters.BoardAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentHomeBoardListBinding

class HomeBoardListFragment : Fragment() {
    private var _binding: FragmentHomeBoardListBinding? = null
    private val model: HomeViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBoardListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val boardListBtn = root.findViewById<Button>(R.id.homepage_board_list_all_btn)
        val navOptions = NavOptions.Builder()
            .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
            .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
            .setPopEnterAnim(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim)
            .setPopExitAnim(androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)

        boardListBtn.setOnClickListener {
            findNavController(this).navigate(R.id.boardListFragment, null, navOptions.build())
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = binding.root.findViewById<RecyclerView>(R.id.homepage_board_list_lv)
        val adapter = BoardAdapter()

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        model.boards.observe(viewLifecycleOwner) {
            adapter.apply {
                submitList(if (it.size > 4) it.subList(0, 3) else it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
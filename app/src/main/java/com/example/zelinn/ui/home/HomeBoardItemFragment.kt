package com.example.zelinn.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.zelinn.BoardActivity
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentHomeBoardItemBinding

class HomeBoardItemFragment: Fragment() {
    private var _binding: FragmentHomeBoardItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val image = ""
    private val title = ""

    companion object {
        fun newInstance(board: BoardModel) = HomeBoardItemFragment().apply {
            arguments = Bundle().apply {
                val fragment = HomeBoardItemFragment()
                val args = Bundle()
                args.putString("image", board.thumbnail)
                args.putString("name", board.name)
                fragment.arguments = args
                return fragment
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBoardItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
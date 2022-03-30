package com.example.zelinn.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentHomeBinding
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
                args.putString("image", board.image)
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
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBoardItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.homepage_board_item_title)

        title.text = arguments?.getString("name")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
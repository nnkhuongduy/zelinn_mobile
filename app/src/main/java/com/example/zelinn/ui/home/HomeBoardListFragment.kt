package com.example.zelinn.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.ui.board_list.BoardListFragment
import com.example.zelinn.R
import com.example.zelinn.adapters.BoardAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentHomeBoardListBinding

class HomeBoardListFragment : Fragment() {
    private var _binding: FragmentHomeBoardListBinding? = null

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

        boardListBtn.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_navigation_home_to_boardListFragment)
        }

        fetchBoards(root)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchBoards(root: View) {
//        RetrofitInstance.retrofit.getBoards().enqueue(object: Callback<List<BoardModel>> {
//            override fun onResponse(
//                call: Call<List<BoardModel>>,
//                response: Response<List<BoardModel>>
//            ) {
//                val boards = response.body()
//
//                boards?.let {
//                    for (board in boards) {
//                        val item = HomeBoardItemFragment.newInstance(board)
//
//                        supportFragmentManager.beginTransaction().add(R.id.homepage_board_list_item_layout, item, board.id).commit()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {
//
//            }
//        })

        val rv = root.findViewById<RecyclerView>(R.id.homepage_board_list_lv)

        val board1 = BoardModel(
            "1",
            "Design Plan",
            "https://static01.nyt.com/images/2021/09/14/science/07CAT-STRIPES/07CAT-STRIPES-mediumSquareAt3X-v2.jpg"
        )
        val board2 = BoardModel(
            "2",
            "Dev Plan",
            "https://static01.nyt.com/images/2021/09/14/science/07CAT-STRIPES/07CAT-STRIPES-mediumSquareAt3X-v2.jpg"
        )
        val board3 = BoardModel(
            "3",
            "Marketing",
            "https://static01.nyt.com/images/2021/09/14/science/07CAT-STRIPES/07CAT-STRIPES-mediumSquareAt3X-v2.jpg"
        )
        val board4 = BoardModel(
            "4",
            "Finan Plan",
            "https://static01.nyt.com/images/2021/09/14/science/07CAT-STRIPES/07CAT-STRIPES-mediumSquareAt3X-v2.jpg"
        )
        val boards = listOf<BoardModel>(board1, board2, board3, board4)
        val adapter = BoardAdapter()

        rv.layoutManager = LinearLayoutManager(root.context)
        rv.adapter = adapter

        adapter.apply {
            submitList(boards)
        }
    }
}
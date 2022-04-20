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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.adapters.BoardAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.databinding.FragmentBoardListBinding

class BoardListFragment : Fragment() {
    private lateinit var backdrop: FrameLayout
    private lateinit var backPressedCallback: OnBackPressedCallback

    private var _binding: FragmentBoardListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        backBtn.setOnClickListener {
            Navigation.findNavController(root).popBackStack()
        }
        createBtn.setOnClickListener {
            createBoardCreateFragment()
        }

        fetchBoards()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createBoardCreateFragment() {
        val createBoardF = CreateBoardFragment.newInstance()
        val activity = requireActivity() as HomeActivity

        backdrop.visibility = View.VISIBLE
        activity.setBottomNavVisibility(View.INVISIBLE)
        activity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

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

        activity.supportFragmentManager.popBackStack()

        if (activity.supportFragmentManager.backStackEntryCount == 1) {
            backdrop.visibility = View.INVISIBLE
            backPressedCallback.remove()
            activity.setBottomNavVisibility(View.VISIBLE)
        }
    }

    private fun fetchBoards() {
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

        val rv = binding.root.findViewById<RecyclerView>(R.id.board_list_rv)

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

        rv.layoutManager = LinearLayoutManager(binding.root.context)
        rv.adapter = adapter

        adapter.apply {
            submitList(boards)
        }
    }
}
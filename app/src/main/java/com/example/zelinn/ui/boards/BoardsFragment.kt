package com.example.zelinn.ui.boards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentBoardsBinding
import com.example.zelinn.ui.boards.create.CreateBoardFragment
import io.ak1.BubbleTabBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardsFragment : Fragment() {
    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var listView: RecyclerView
    private lateinit var createBtn: Button
    private lateinit var backdrop: CardView
    private lateinit var fragmentView: FragmentContainerView

    private var _binding: FragmentBoardsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: BoardsViewModel by activityViewModels()
    private val adapter = BoardAdapter()
    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            this.remove()
            backdrop.visibility = View.GONE

            requireActivity().supportFragmentManager.popBackStack(CreateBoardFragment.TAG,  FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBoardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        findViews()
        init()
        watch()

//        ZelinnApp.prefs.push("key", "encrypted value")

//        val textView: TextView = binding.textView
//        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
//        textView.text = "${getString(R.string.homepage_gretting)} ${if (user.name.length > 10) user.name.substring(0, 10) + "..." else user.name} \uD83D\uDC4B"

        return root
    }

    override fun onResume() {
        super.onResume()

        val flag = ZelinnApp.prefs.pull(getString(R.string.preference_boards_flag), false)

        if (flag) getBoards()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        this.swipeLayout = binding.root.findViewById(R.id.boards_list_swipe)
        this.listView = binding.root.findViewById(R.id.boards_list_lv)
        this.createBtn = binding.root.findViewById(R.id.boards_create_btn)
        this.backdrop = binding.root.findViewById(R.id.boards_backdrop)
        this.fragmentView = binding.root.findViewById(R.id.boards_fragment_container)
    }

    private fun init() {
        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_boards, false)

        this.listView.layoutManager = LinearLayoutManager(requireContext())
        this.listView.adapter = adapter

        getBoards()
    }

    private fun watch() {
        model.boardsFlag.observe(viewLifecycleOwner) {
            if (it) getBoards()
        }
        model.boards.observe(viewLifecycleOwner) {
            adapter.apply {
                submitList(it)
            }
        }

        this.swipeLayout.setOnRefreshListener {
            getBoards()
        }
        this.createBtn.setOnClickListener {
            val permissionFragment = CreateBoardFragment()
            val activity = requireActivity()
            this.backdrop.visibility = View.VISIBLE

            activity.onBackPressedDispatcher.addCallback(backPressedCallback)
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                )
                .replace(R.id.boards_fragment_container, permissionFragment)
                .addToBackStack("CREATE_BOARD")
                .commit()
        }
    }

    private fun getBoards() {
        RetrofitInstance.retrofit.getBoards().enqueue(object: Callback<List<BoardModel>> {
            override fun onResponse(
                call: Call<List<BoardModel>>,
                response: Response<List<BoardModel>>
            ) {
                val boards = response.body()
                if (response.isSuccessful && boards != null) {
                    model.setBoards(boards)
                    model.setBoardsFlag(false)
                    ZelinnApp.prefs.push(getString(R.string.preference_boards_flag), false)

                    this@BoardsFragment.swipeLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {}
        })
    }
}
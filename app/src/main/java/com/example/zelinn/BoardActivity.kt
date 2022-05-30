package com.example.zelinn

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.ui.list.BoardListAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.ListModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.ui.board.BoardMenuFragment
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.board.card.CardViewModel
import com.example.zelinn.ui.board.card.CreateCardFragment
import com.example.zelinn.ui.list.CreateListFragment
import com.example.zelinn.ui.list.ListViewModel
import com.example.zelinn.ui.list.edit.ListEditFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardActivity : AppCompatActivity() {
    private lateinit var nameView: TextView
    private lateinit var imageView: ImageView
    private lateinit var backBtn: Button
    private lateinit var menuBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentTag: String

    private val model: BoardViewModel by viewModels()
    private val cardModel: CardViewModel by viewModels()
    private val listModel: ListViewModel by viewModels()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeFragment()
        }
    }
    private val adapter = BoardListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        ZelinnApp.prefs.push(getString(R.string.preference_board_flag), false)

        findViews()

        populate()

        watch()
    }

    override fun onResume() {
        super.onResume()

        try {
            val flag = ZelinnApp.prefs.pull(getString(R.string.preference_board_flag), false)

            if (flag) getBoard()
        } catch (e: Exception) {}
    }

    private fun findViews() {
        nameView = findViewById(R.id.board_name_view)
        imageView = findViewById(R.id.board_image_view)
        backBtn = findViewById(R.id.board_back_btn)
        menuBtn = findViewById(R.id.board_info_btn)
        recyclerView = findViewById(R.id.board_activity_list_rv)
    }

    private fun watch() {
        backBtn.setOnClickListener {
            onBackPressed()
        }
        menuBtn.setOnClickListener {
            currentTag = "BOARD_MENU"
            val fragment = BoardMenuFragment()
            createFragment(fragment)
        }
        model.board.observe(this) {
            nameView.text = it.name
            Glide.with(this).load(it.thumbnail).into(imageView)
            getLists()
        }
        model.boardFlag.observe(this) {
            getBoard()
        }
        model.listFlag.observe(this) {
            getLists()
        }
        model.lists.observe(this) {
            adapter.apply {
                submitLists(it) {
                    recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    private fun populate() {
        adapter.addCardCallback = {
            cardModel.reset(it)
            currentTag = "BOARD_CREATE_CARD"
            val fragment = CreateCardFragment()
            createFragment(fragment)
        }
        adapter.addListCallback = {
            currentTag = "BOARD_CREATE_LIST"
            val fragment = CreateListFragment()
            createFragment(fragment)
        }
        adapter.editListCallback = {
            currentTag = "BOARD_EDIT_LIST"
            val fragment = ListEditFragment()
            createFragment(fragment)

            listModel.setList(it)
        }

        recyclerView.layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun toggleBackdrop() {
        val backdrop = findViewById<FrameLayout>(R.id.board_activity_backdrop)

        backdrop.visibility =
            if (backdrop.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    private fun createFragment(fragment: Fragment) {
        toggleBackdrop()

        onBackPressedDispatcher.addCallback(backPressedCallback)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, fragment, currentTag)
            .addToBackStack(currentTag)
            .commit()
    }

    private fun closeFragment() {
        backPressedCallback.remove()
        toggleBackdrop()

        supportFragmentManager.popBackStack(currentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun getBoard() {
        val bundle = intent.extras
        val board = bundle?.get("board") as BoardModel

        RetrofitInstance.retrofit.getBoard(board.id).enqueue(object : Callback<BoardModel> {
            override fun onResponse(call: Call<BoardModel>, response: Response<BoardModel>) {
                val board = response.body()

                if (response.isSuccessful && board != null) {
                    model.setBoard(board)
                } else {
                    onBackPressed()
                }
            }

            override fun onFailure(call: Call<BoardModel>, t: Throwable) {
                onBackPressed()
            }
        })
    }

    private fun getLists() {
        val board = model.board.value ?: return

        RetrofitInstance.retrofit.getLists(board.id).enqueue(object : Callback<List<ListModel>> {
            override fun onResponse(
                call: Call<List<ListModel>>,
                response: Response<List<ListModel>>
            ) {
                val lists = response.body()

                if (response.isSuccessful && lists != null) {
                    model.setLists(lists)
                } else onBackPressed()
            }

            override fun onFailure(call: Call<List<ListModel>>, t: Throwable) {
                onBackPressed()
            }
        })
    }
}
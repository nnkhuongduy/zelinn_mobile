package com.example.zelinn

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.adapters.ListAdapter
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.ListModel
import com.example.zelinn.ui.board.BoardMenuFragment
import com.example.zelinn.ui.board.BoardViewModel


class BoardActivity : AppCompatActivity() {
    private val model: BoardViewModel by viewModels()
    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeMenuFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        val bundle = intent.extras
        val board = bundle?.get("board") as BoardModel
        val nameView = findViewById<TextView>(R.id.board_name_view)
        val imageView = findViewById<ImageView>(R.id.board_image_view)
        val backBtn = findViewById<Button>(R.id.board_back_btn)
        val menuBtn = findViewById<Button>(R.id.board_info_btn)

        model.setBoard(board)
        nameView.text = board.name
        Glide.with(this).load(board.thumbnail).into(imageView)

        backBtn.setOnClickListener {
            onBackPressed()
        }
        menuBtn.setOnClickListener {
            createMenuFragment()
        }

        fetchLists()
    }

    private fun fetchLists() {
        val rv = findViewById<RecyclerView>(R.id.board_activity_list_rv)

        val list1 = ListModel(
            "1",
            "Backlog",
        )
        val list2 = ListModel(
            "2",
            "Sắp thực hiện",
        )
        val list3 = ListModel(
            "3",
            "Đang thực hiện",
        )
        val list4 = ListModel(
            "4",
            "Đã hoàn tất",
        )
        val lists = mutableListOf<ListModel>(list1, list2, list3, list4)
        val adapter = ListAdapter(this)

        rv.layoutManager = LinearLayoutManager(this.applicationContext, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = adapter

        adapter.apply {
            submitList(lists)
        }
    }

    fun toggleBackdrop() {
        val backdrop = findViewById<FrameLayout >(R.id.board_activity_backdrop)

        backdrop.visibility = if (backdrop.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
    }

    private fun createMenuFragment() {
        toggleBackdrop()

        val fragment = BoardMenuFragment()

        onBackPressedDispatcher.addCallback(backPressedCallback)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, fragment, "BOARD_MENU")
            .addToBackStack("BOARD_MENU")
            .commit()
    }

    private fun closeMenuFragment() {
        backPressedCallback.remove()
        toggleBackdrop()

        supportFragmentManager.popBackStack("BOARD_MENU", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
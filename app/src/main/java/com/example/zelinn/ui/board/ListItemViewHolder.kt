package com.example.zelinn.ui.board

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.BoardActivity
import com.example.zelinn.R
import com.example.zelinn.adapters.CardAdapter
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.ListModel
import com.example.zelinn.databinding.FragmentListItemBinding

class ListItemViewHolder(itemView: View, private val activity: AppCompatActivity) : RecyclerView.ViewHolder(itemView) {
    private lateinit var backPressedCallback: OnBackPressedCallback

    private fun createCreateFragment(fragment: Fragment, tag: String) {
        (activity as BoardActivity).toggleBackdrop()
        activity.onBackPressedDispatcher.addCallback(itemView.findViewTreeLifecycleOwner()!!, backPressedCallback)
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

    private fun createCreateListFragment() {
        val fragment = CreateListFragment.newInstance()
        createCreateFragment(fragment, "CREATE_LIST")
    }

    private fun createCreateCardFragment() {
        val fragment = CreateCardFragment.newInstance()
        createCreateFragment(fragment, "CREATE_CARD")
    }

    private fun closeCreateFragment() {
        activity.supportFragmentManager.popBackStack()

        if (activity.supportFragmentManager.backStackEntryCount == 1) {
            (activity as BoardActivity).toggleBackdrop()
            backPressedCallback.remove()
        }
    }

    private fun fetchCards() {
        val rv = itemView.findViewById<RecyclerView>(R.id.list_item_rv)

        val card1 = CardModel(
            "1",
            "Lập trình Python",
            "2021-12-07T13:03:42.064+00:00",
            null
        )
        val card2 = CardModel(
            "2",
            "Lên wireframe",
            "2021-12-09T13:03:42.064+00:00",
            "https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png"
        )
        val cards = mutableListOf<CardModel>(card1, card2)
        val adapter = CardAdapter(activity)

        rv.layoutManager = LinearLayoutManager(itemView.context)
        rv.adapter = adapter

        adapter.apply {
            submitList(cards)
        }
    }

    fun onBind(list: ListModel) {
        val nameView = itemView.findViewById<TextView>(R.id.list_name_view)
        val layoutView = itemView.findViewById<LinearLayout>(R.id.list_item_layout)
        val addCardBtnView = itemView.findViewById<ImageButton>(R.id.list_item_add_card_btn)
        val addListBtnView = itemView.findViewById<Button>(R.id.list_item_create_btn)
        val rv = itemView.findViewById<RecyclerView>(R.id.list_item_rv)

        backPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeCreateFragment()
            }
        }

        if (list.id == "ADDING") {
            layoutView.visibility = View.INVISIBLE
            addCardBtnView.visibility = View.INVISIBLE
            rv.visibility = View.GONE

            addListBtnView.visibility = View.VISIBLE

        } else {
            nameView.text = list.name
            fetchCards()
        }

        addListBtnView.setOnClickListener {
            createCreateListFragment()
        }
        addCardBtnView.setOnClickListener {
            createCreateCardFragment()
        }
    }
}
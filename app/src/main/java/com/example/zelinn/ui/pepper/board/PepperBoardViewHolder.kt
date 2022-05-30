package com.example.zelinn.ui.pepper.board

import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.OnSwipeTouchListener
import com.example.zelinn.classes.PepperModel
import com.example.zelinn.ui.card.CardAdapter
import com.example.zelinn.ui.pepper.PepperFragment
import com.example.zelinn.ui.pepper.list.PepperListAdapter
import com.google.android.material.card.MaterialCardView

class PepperBoardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var boardBtn: Button
    private lateinit var cardView: MaterialCardView
    private lateinit var listView: RecyclerView

    private lateinit var pepper: PepperModel

    private var adapter = PepperListAdapter()

    fun onBind(pepper: PepperModel) {
        this.pepper = pepper

        findViews()
        init()
        watch()
    }

    private fun findViews() {
        this.boardBtn = itemView.findViewById(R.id.pepper_board_btn)
        this.cardView = itemView.findViewById(R.id.pepper_board_card)
        this.listView = itemView.findViewById(R.id.pepper_board_list)
    }

    private fun init() {
        updateState()

        this.boardBtn.text = this.pepper.name + if (pepper.faved) " \uD83C\uDF1F" else ""
        this.listView.layoutManager = LinearLayoutManager(itemView.context)
        this.listView.adapter = adapter

        adapter.apply {
            submitList(pepper.lists)
        }
    }

    private fun watch() {
        this.boardBtn.setOnClickListener {
            pepper.expanded = !pepper.expanded

            updateState()
        }
    }

    private fun updateState() {
        val icon = if (pepper.expanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        this.boardBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, icon, 0)
        this.cardView.visibility = if (pepper.expanded) View.VISIBLE else View.GONE
    }
}
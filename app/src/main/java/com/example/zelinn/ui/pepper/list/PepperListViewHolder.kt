package com.example.zelinn.ui.pepper.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.PepperListModel
import com.example.zelinn.ui.card.CardAdapter

class PepperListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var nameView: TextView
    private lateinit var listView: RecyclerView

    private lateinit var list: PepperListModel

    private val adapter = CardAdapter()

    fun onBind(list: PepperListModel) {
        this.list = list

        findViews()
        init()
    }

    private fun findViews() {
        this.nameView = itemView.findViewById(R.id.pepper_list_name)
        this.listView = itemView.findViewById(R.id.pepper_list_rv)
    }

    private fun init() {
        this.nameView.text = this.list.name

        this.listView.layoutManager = LinearLayoutManager(itemView.context)
        this.listView.adapter = adapter

        adapter.apply {
            submitList(this@PepperListViewHolder.list.cards)
        }
    }
}
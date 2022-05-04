package com.example.zelinn.ui.board.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.adapters.CardItemCallback
import com.example.zelinn.classes.CardModel

class CardAdapter : ListAdapter<CardModel, CardItemViewHolder>(CardItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val root =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item, parent, false)

        return CardItemViewHolder(root)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
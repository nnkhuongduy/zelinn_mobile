package com.example.zelinn.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.MemberModel

class MemberItemCallback: DiffUtil.ItemCallback<MemberModel>() {
    override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }
}

class CardItemCallback: DiffUtil.ItemCallback<CardModel>() {
    override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }
}
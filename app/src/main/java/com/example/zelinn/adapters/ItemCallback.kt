package com.example.zelinn.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.PepperListModel
import com.example.zelinn.classes.PepperModel

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

class PepperItemCallback : DiffUtil.ItemCallback<PepperModel>() {
    override fun areContentsTheSame(oldItem: PepperModel, newItem: PepperModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areItemsTheSame(oldItem: PepperModel, newItem: PepperModel): Boolean {
        return oldItem.id == newItem.id
    }
}

class PepperListItemCallback : DiffUtil.ItemCallback<PepperListModel>() {
    override fun areContentsTheSame(oldItem: PepperListModel, newItem: PepperListModel): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areItemsTheSame(oldItem: PepperListModel, newItem: PepperListModel): Boolean {
        return oldItem.id == newItem.id
    }
}
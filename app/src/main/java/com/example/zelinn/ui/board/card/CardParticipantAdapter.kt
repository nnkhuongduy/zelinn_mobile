package com.example.zelinn.ui.board.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class CardParticipantAdapter: ListAdapter<MemberModel, CardParticipantViewHolder>(ParticipantItemCallback()) {
    private class ParticipantItemCallback: DiffUtil.ItemCallback<MemberModel>() {
        override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardParticipantViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_user_avatar, parent, false)

        return CardParticipantViewHolder(root)
    }

    override fun onBindViewHolder(holder: CardParticipantViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
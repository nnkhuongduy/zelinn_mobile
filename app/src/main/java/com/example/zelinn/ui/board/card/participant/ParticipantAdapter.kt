package com.example.zelinn.ui.board.card.participant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.adapters.MemberItemCallback
import com.example.zelinn.classes.MemberModel

class ParticipantAdapter: ListAdapter<MemberModel, ParticipantViewHolder>(MemberItemCallback()) {
    private lateinit var onItemClickListener: ((MemberModel, Int) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_member_item_selectable, parent, false)

        return ParticipantViewHolder(root, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }

    fun setOnItemClickListener(listener: ((MemberModel, Int) -> Unit)) {
        onItemClickListener = listener
    }
}
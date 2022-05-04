package com.example.zelinn.ui.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class BoardMenuMemberAdapter(): ListAdapter<MemberModel, BoardMenuMemberViewHolder>(BoardMenuMemberItemCallback()) {
    private class BoardMenuMemberItemCallback: DiffUtil.ItemCallback<MemberModel>() {
        override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMenuMemberViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_user_avatar, parent, false)

        return BoardMenuMemberViewHolder(root)
    }

    override fun onBindViewHolder(holder: BoardMenuMemberViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
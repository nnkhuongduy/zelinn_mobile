package com.example.zelinn.ui.board.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class BoardMenuInviteAdapter(): ListAdapter<MemberModel, BoardMenuInviteViewHolder>(MemberItemCallback()) {
    var onItemClickListener: ((MemberModel, Boolean) -> Unit)? = null

    private class MemberItemCallback: DiffUtil.ItemCallback<MemberModel>() {
        override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMenuInviteViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_member_item_selectable, parent, false)

        return BoardMenuInviteViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: BoardMenuInviteViewHolder, position: Int) {
        holder.onBind(getItem(position), onItemClickListener)
    }
}
package com.example.zelinn.ui.board.member_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class BoardMemberListAdapter: ListAdapter<MemberModel, BoardMemberListViewHolder>(BoardMemberListItemCallback()) {
    var onItemClickListener: ((MemberModel, Boolean) -> Unit)? = null
    var selectable: Boolean = false

    private class BoardMemberListItemCallback: DiffUtil.ItemCallback<MemberModel>() {
        override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMemberListViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_member_item_selectable, parent, false)

        return BoardMemberListViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: BoardMemberListViewHolder, position: Int) {
        holder.onBind(getItem(position), onItemClickListener, selectable)
    }
}
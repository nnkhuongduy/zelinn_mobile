package com.example.zelinn.ui.board.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.UserModel

class BoardMenuOwnerAdapter(): ListAdapter<MemberModel, BoardMenuOwnerViewHolder>(MemberItemCallback()) {
    var itemClickCallback: ((member: MemberModel) -> Unit)? = null
    var owner: UserModel? = null

    private class MemberItemCallback: DiffUtil.ItemCallback<MemberModel>() {
        override fun areContentsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: MemberModel, newItem: MemberModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardMenuOwnerViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_member_item_selectable, parent, false)

        return BoardMenuOwnerViewHolder(root)
    }

    override fun onBindViewHolder(holder: BoardMenuOwnerViewHolder, position: Int) {
        holder.onBind(getItem(position), owner, itemClickCallback)
    }
}
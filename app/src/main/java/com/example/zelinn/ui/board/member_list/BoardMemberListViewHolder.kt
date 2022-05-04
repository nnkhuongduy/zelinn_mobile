package com.example.zelinn.ui.board.member_list

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.UserModel
import com.google.android.material.card.MaterialCardView
import com.orhanobut.hawk.Hawk

class BoardMemberListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var selectBtn: Button
    private lateinit var removingBtn: Button
    private lateinit var cardView: MaterialCardView
    private lateinit var pendingView: TextView

    private var selected: Boolean = false

    fun onBind(
        member: MemberModel,
        onClickListener: ((MemberModel, Boolean) -> Unit)?,
        selectable: Boolean
    ) {
        avatarView = itemView.findViewById(R.id.member_item_selectable_avatar)
        nameText = itemView.findViewById(R.id.member_item_selectable_name)
        emailText = itemView.findViewById(R.id.member_item_selectable_email)
        selectBtn = itemView.findViewById(R.id.member_item_selectable_btn)
        removingBtn = itemView.findViewById(R.id.member_item_selectable_btn_removing)
        cardView = itemView.findViewById(R.id.member_item_selectable_card)
        pendingView = itemView.findViewById(R.id.member_item_selectable_pending)

        if (member.avatar.isNullOrEmpty()) avatarView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(member.avatar).into(avatarView)

        nameText.text = member.name
        pendingView.visibility = if (member.pending) View.VISIBLE else View.GONE
        selectBtn.visibility = if (selectable && member.id != Hawk.get<UserModel>(itemView.resources.getString(R.string.preference_current_user)).id && !member.pending) View.VISIBLE else View.GONE
        emailText.visibility = if (selectable) View.VISIBLE else View.GONE
        emailText.text = member.email

        selectBtn.setOnClickListener {
            toggleSelectState()
            onClickListener?.invoke(member, selected)
        }
        removingBtn.setOnClickListener {
            toggleSelectState()
            onClickListener?.invoke(member, selected)
        }
    }

    private fun toggleSelectState() {
        selected = !selected

        if (selected) {
            selectBtn.visibility = View.GONE
            removingBtn.visibility = View.VISIBLE
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.danger)
        } else {
            selectBtn.visibility = View.VISIBLE
            removingBtn.visibility = View.GONE
            cardView.strokeColor =
                ContextCompat.getColor(itemView.context, R.color.background_surface)
        }
    }
}
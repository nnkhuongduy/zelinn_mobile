package com.example.zelinn.ui.board.invite

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.google.android.material.card.MaterialCardView

class BoardMenuInviteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var selectBtn: Button
    private lateinit var seletedBtn: Button
    private lateinit var cardView: MaterialCardView
    private lateinit var pendingView: TextView

    private var selected: Boolean = false

    fun onBind(member: MemberModel, onClickListener: ((MemberModel, Boolean) -> Unit)?) {
        avatarView = itemView.findViewById(R.id.member_item_selectable_avatar)
        nameText = itemView.findViewById(R.id.member_item_selectable_name)
        emailText = itemView.findViewById(R.id.member_item_selectable_email)
        selectBtn = itemView.findViewById(R.id.member_item_selectable_btn)
        seletedBtn = itemView.findViewById(R.id.member_item_selectable_btn_checked)
        cardView = itemView.findViewById(R.id.member_item_selectable_card)
        pendingView = itemView.findViewById(R.id.member_item_selectable_pending)

        if (member.avatar.isNullOrEmpty()) avatarView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(member.avatar).into(avatarView)

        nameText.text = member.name
        emailText.text = member.email

        if (member.pending) {
            pendingView.visibility = View.VISIBLE
            selectBtn.visibility = View.INVISIBLE
        } else {
            pendingView.visibility = View.INVISIBLE
            selectBtn.visibility = View.VISIBLE
        }

        selectBtn.setOnClickListener {
            toggleSelectState()
            onClickListener?.invoke(member, selected)
        }
        seletedBtn.setOnClickListener {
            toggleSelectState()
            onClickListener?.invoke(member, selected)
        }
    }

    private fun toggleSelectState() {
        selected = !selected

        if (selected) {
            selectBtn.visibility = View.GONE
            seletedBtn.visibility = View.VISIBLE
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.primary)
        } else {
            selectBtn.visibility = View.VISIBLE
            seletedBtn.visibility = View.GONE
            cardView.strokeColor =
                ContextCompat.getColor(itemView.context, R.color.background_surface)
        }
    }
}
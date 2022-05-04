package com.example.zelinn.ui.board.card.participant

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

class ParticipantViewHolder(itemView: View, val onItemClickListener: ((MemberModel, Int) -> Unit)) : RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var selectBtn: Button
    private lateinit var selectedBtn: Button
    private lateinit var cardView: MaterialCardView
    private lateinit var member: MemberModel

    fun onBind(member: MemberModel, position: Int) {
        this.member = member

        findViews()
        populate()
        watch(position)
    }

    private fun findViews() {
        avatarView = itemView.findViewById(R.id.member_item_selectable_avatar)
        nameText = itemView.findViewById(R.id.member_item_selectable_name)
        emailText = itemView.findViewById(R.id.member_item_selectable_email)
        selectBtn = itemView.findViewById(R.id.member_item_selectable_btn)
        selectedBtn = itemView.findViewById(R.id.member_item_selectable_btn_checked)
        cardView = itemView.findViewById(R.id.member_item_selectable_card)
    }

    private fun populate() {
        emailText.visibility = View.GONE
        nameText.text = member.name

        if (member.avatar.isNullOrEmpty()) avatarView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(member.avatar).into(avatarView)

        selectBtn.visibility = if (member.selected) View.GONE else View.VISIBLE
        selectedBtn.visibility = if (member.selected) View.VISIBLE else View.GONE
        cardView.strokeColor = ContextCompat.getColor(
            itemView.context,
            if (member.selected) R.color.primary else R.color.background_surface
        )
    }

    private fun watch(position: Int) {
        selectBtn.setOnClickListener {
            onItemClickListener(member, position)
        }
        selectedBtn.setOnClickListener {
            onItemClickListener(member, position)
        }
    }
}
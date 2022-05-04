package com.example.zelinn.ui.board.info

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.UserModel
import com.google.android.material.card.MaterialCardView

class BoardMenuOwnerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView
    private lateinit var nameText: TextView
    private lateinit var emailText: TextView
    private lateinit var selectBtn: Button
    private lateinit var selectedBtn: Button
    private lateinit var cardView: MaterialCardView

    fun onBind(member: MemberModel, owner: UserModel?, callback: ((MemberModel) -> Unit)?) {
        avatarView = itemView.findViewById(R.id.member_item_selectable_avatar)
        nameText = itemView.findViewById(R.id.member_item_selectable_name)
        emailText = itemView.findViewById(R.id.member_item_selectable_email)
        selectBtn = itemView.findViewById(R.id.member_item_selectable_btn)
        selectedBtn = itemView.findViewById(R.id.member_item_selectable_btn_checked)
        cardView = itemView.findViewById(R.id.member_item_selectable_card)

        if (member.avatar.isNullOrEmpty()) avatarView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(member.avatar).into(avatarView)

        nameText.text = member.name
        emailText.text = member.email

        val isSelected = member.id == owner?.id

        selectBtn.visibility = if (isSelected) View.GONE else View.VISIBLE
        selectedBtn.visibility = if (isSelected) View.VISIBLE else View.GONE

        selectBtn.setOnClickListener {
            callback?.invoke(member)
        }
        selectedBtn.setOnClickListener {
            callback?.invoke(member)
        }
    }
}
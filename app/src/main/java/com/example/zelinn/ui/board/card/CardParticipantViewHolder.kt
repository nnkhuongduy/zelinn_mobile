package com.example.zelinn.ui.board.card

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class CardParticipantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView

    fun onBind(member: MemberModel) {
        avatarView = itemView.findViewById(R.id.user_avatar_view)

        if (member.avatar.isNullOrEmpty()) avatarView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(member.avatar).into(avatarView)
    }
}
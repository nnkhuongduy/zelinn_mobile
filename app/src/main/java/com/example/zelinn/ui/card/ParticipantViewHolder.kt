package com.example.zelinn.ui.card

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel

class ParticipantViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var avatarView: ImageView

    private lateinit var member: MemberModel

    fun onBind(member: MemberModel) {
        this.member = member

        findViews()
        init()
    }

    private fun findViews() {
        avatarView = itemView.findViewById(R.id.user_avatar_view)
    }

    private fun init() {
        if (!member.avatar.isNullOrEmpty())
            Glide.with(itemView).load(member.avatar).into(avatarView)
        else avatarView.setImageResource(R.drawable.ic_person)
    }
}
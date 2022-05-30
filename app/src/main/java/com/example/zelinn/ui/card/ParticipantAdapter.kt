package com.example.zelinn.ui.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.adapters.MemberItemCallback
import com.example.zelinn.classes.MemberModel

class ParticipantAdapter: ListAdapter<MemberModel, ParticipantViewHolder>(MemberItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val root =
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_user_avatar, parent, false)

        return ParticipantViewHolder(root)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
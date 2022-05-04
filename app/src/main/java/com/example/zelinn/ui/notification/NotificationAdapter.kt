package com.example.zelinn.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.classes.NotificationModel

class NotificationAdapter(): ListAdapter<NotificationModel, NotificationItemViewHolder>(NotificationItemCallback()) {
    var clickCallback: ((NotificationModel) -> Unit)? = null

    private class NotificationItemCallback: DiffUtil.ItemCallback<NotificationModel>() {
        override fun areContentsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationItemViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_notification_item, parent, false)

        return NotificationItemViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: NotificationItemViewHolder, position: Int) {
        holder.onBind(getItem(position), clickCallback)
    }
}
package com.example.zelinn.ui.notification

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.NotificationModel
import java.text.SimpleDateFormat
import java.util.*

class NotificationItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private lateinit var titleText: TextView
    private lateinit var labelText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var dateText: TextView
    private lateinit var iconView: ImageView
    private lateinit var cardView: CardView
    private lateinit var confirmText: TextView

    private var descriptionState = false
    private var notification: NotificationModel? = null

    fun onBind(notification: NotificationModel, clickCallback: ((NotificationModel) -> Unit)?) {
        this.notification = notification

        titleText = itemView.findViewById(R.id.notification_item_title)
        labelText = itemView.findViewById(R.id.notification_item_label)
        descriptionText = itemView.findViewById(R.id.notification_item_description)
        dateText = itemView.findViewById(R.id.notification_item_date)
        iconView = itemView.findViewById(R.id.notification_item_icon)
        cardView = itemView.findViewById(R.id.notification_item_card)
        confirmText = itemView.findViewById(R.id.notification_item_confirm_text)

        titleText.text = notification.title
        labelText.text = notification.label
        descriptionText.text = if (notification.description.length > 50) notification.description.substring(0..50) + "..." else notification.description
        dateText.text = SimpleDateFormat("HH':'mm E dd").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH).parse(notification.createdAt))

        confirmText.visibility = if (notification.type == itemView.resources.getString(R.string.notification_type_invite_board)) View.VISIBLE else View.GONE
        if (notification.icon != null) {
            Glide.with(itemView).load(notification.icon).into(iconView)
        } else {
            iconView.visibility = View.INVISIBLE
        }

        cardView.setOnClickListener {
            clickCallback?.invoke(notification)
            toggleDescriptionState()
        }

        if (notification.seen) {
            titleText.setTextColor(itemView.resources.getColor(R.color.gray3))
            labelText.setTextColor(itemView.resources.getColor(R.color.gray3))
            descriptionText.setTextColor(itemView.resources.getColor(R.color.gray3))
            dateText.setTextColor(itemView.resources.getColor(R.color.gray3))
            confirmText.visibility = View.GONE
        }
    }

    private fun toggleDescriptionState() {
        val notification = notification?: return
        descriptionState = !descriptionState

        descriptionText.text = if (!descriptionState && notification.description.length > 50) notification.description.substring(0..50) + "..." else notification.description
    }
}
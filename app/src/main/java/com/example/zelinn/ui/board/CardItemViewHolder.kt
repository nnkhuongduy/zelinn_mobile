package com.example.zelinn.ui.board

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.CardModel
import java.text.SimpleDateFormat
import java.util.*

class CardItemViewHolder(itemView: View, private val activity: AppCompatActivity) : RecyclerView.ViewHolder(itemView) {

    fun onBind(card: CardModel) {
        val thumbnailView = itemView.findViewById<ImageView>(R.id.card_item_thumbnail)
        val titleView = itemView.findViewById<TextView>(R.id.card_item_title)
        val dueView = itemView.findViewById<TextView>(R.id.card_item_due)
        val assignmentView = itemView.findViewById<ImageView>(R.id.card_item_assignment)

        if (card.thumbnail.isNullOrEmpty()) {
            thumbnailView.visibility = View.GONE
        } else {
            Glide.with(itemView).load(card.thumbnail).into(thumbnailView)
        }

        titleView.text = card.name
        dueView.text = SimpleDateFormat("'Ngày' d 'tháng' M").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH).parse(card.due))
        Glide.with(itemView).load("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/VAN_CAT.png/1024px-VAN_CAT.png").into(assignmentView)
    }

}
package com.example.zelinn.ui.board.list

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cesarferreira.tempo.Tempo
import com.example.zelinn.CardActivity
import com.example.zelinn.R
import com.example.zelinn.classes.CardModel
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat

class CardItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var thumbnailView: ImageView
    private lateinit var titleView: TextView
    private lateinit var dueView: TextView
    private lateinit var assignmentView: ImageView
    private lateinit var layoutView: LinearLayout
    private lateinit var cardView: MaterialCardView

    private lateinit var card: CardModel

    fun onBind(card: CardModel) {
        this.card = card

        findViews()

        populate()

        watch()
    }

    private fun findViews() {
        thumbnailView = itemView.findViewById<ImageView>(R.id.card_item_thumbnail)
        titleView = itemView.findViewById<TextView>(R.id.card_item_title)
        dueView = itemView.findViewById<TextView>(R.id.card_item_due)
        assignmentView = itemView.findViewById<ImageView>(R.id.card_item_assignment)
        layoutView = itemView.findViewById<LinearLayout>(R.id.card_item_layout)
        cardView = itemView.findViewById(R.id.card_item_card)
    }

    private fun populate() {
        titleView.text = card.name

        if (!card.thumbnail.isNullOrEmpty())
            Glide.with(itemView).load(card.thumbnail).into(thumbnailView)
        else thumbnailView.visibility = View.GONE
        if (!card.participants.isNullOrEmpty()) {
            val first = card.participants[0]

            if (first.avatar.isNullOrEmpty()) assignmentView.setImageResource(R.drawable.ic_person)
            else Glide.with(itemView).load(first.avatar).into(assignmentView)
        } else assignmentView.visibility = View.GONE

        if (card.due != null)
            dueView.text = SimpleDateFormat("'Ngày' d 'tháng' M").format(card.due)
        else dueView.visibility = View.GONE

        if (card.due != null) {
            val diff = (card.due!!.time - Tempo.now.time) / (1000 * 60 * 60)

            if (diff in 0..24)
                cardView.strokeColor =
                    ContextCompat.getColor(itemView.context, R.color.danger)
        }
        if (card.due != null && card.due!!.before(Tempo.now)) {
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.danger)
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.danger))
        }
    }

    private fun watch() {
        layoutView.setOnClickListener {
            val intent = Intent(itemView.context, CardActivity::class.java)

            itemView.context.startActivity(intent)
        }
    }
}
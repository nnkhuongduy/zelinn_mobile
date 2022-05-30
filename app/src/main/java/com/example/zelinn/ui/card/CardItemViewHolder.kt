package com.example.zelinn.ui.card

import android.content.Intent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
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
        checkPriority()
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
        dueView.text = SimpleDateFormat("'Ngày' d 'tháng' M").format(card.due)
        val first = card.participants[0]

        if (first.avatar.isNullOrEmpty()) assignmentView.setImageResource(R.drawable.ic_person)
        else Glide.with(itemView).load(first.avatar).into(assignmentView)

        if (!card.thumbnail.isNullOrEmpty())
            Glide.with(itemView).load(card.thumbnail).into(thumbnailView)
        else thumbnailView.visibility = View.GONE
    }

    private fun watch() {
        layoutView.setOnClickListener {
            val bundle = bundleOf("card" to card)
            val i = Intent(itemView.context, CardActivity::class.java)
            i.putExtras(bundle)

            itemView.context.startActivity(i)
        }
    }

    private fun checkPriority() {
        if (card.completed) return

        val diffHours = (card.due.time - Tempo.now.time) / (1000 * 60 * 60)
        val diffDays = diffHours / 24

        if (diffHours in 0..24 || card.due.before(Tempo.now)) {
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.danger)
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.danger))
            titleView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            dueView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

            return
        }
        if (diffDays in 1..3) {
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.orange)
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.orange))
            titleView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            dueView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

            return
        }
        if (diffDays in 4..7) {
            cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.gold)
            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.gold))
            titleView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            dueView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))

            return
        }
        cardView.strokeColor = ContextCompat.getColor(itemView.context, R.color.primary)
        cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.primary))
        titleView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
        dueView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
    }
}
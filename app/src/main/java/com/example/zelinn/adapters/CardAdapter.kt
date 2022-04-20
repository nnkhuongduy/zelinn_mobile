package com.example.zelinn.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.example.zelinn.classes.CardModel
import com.example.zelinn.ui.board.CardItemViewHolder
import java.text.SimpleDateFormat
import java.util.*

class CardAdapter(private val activity: AppCompatActivity) : ListAdapter<CardModel, CardItemViewHolder>(CardItemCallback()) {
    private class CardItemCallback: DiffUtil.ItemCallback<CardModel>() {
        override fun areContentsTheSame(oldItem: CardModel, newItem: CardModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: CardModel, newItem: CardModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_card_item, parent, false)

        return CardItemViewHolder(root as ViewGroup, activity)
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
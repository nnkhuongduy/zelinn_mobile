package com.example.zelinn.ui.boards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel

class BoardAdapter() : ListAdapter<BoardModel, BoardAdapter.BoardViewHolder>(BoardItemCallback()) {
    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(board: BoardModel) {
            val title = itemView.findViewById<TextView>(R.id.homepage_board_item_title)
            val imageView =
                itemView.findViewById<ImageView>(R.id.homepage_board_item_image)
            val layoutView = itemView.findViewById<LinearLayout>(R.id.home_board_item_layout_view)
            val starView = itemView.findViewById<ImageView>(R.id.homepage_board_item_star)

            title.text = board.name
            Glide.with(itemView).load(board.thumbnail).into(imageView)
            starView.visibility = if (board.faved) View.VISIBLE else View.GONE

            layoutView.setOnClickListener {
                val bundle = bundleOf("board" to board)
                Navigation.findNavController(itemView).navigate(R.id.boardActivity, bundle)
            }
        }
    }

    private class BoardItemCallback : DiffUtil.ItemCallback<BoardModel>() {
        override fun areContentsTheSame(oldItem: BoardModel, newItem: BoardModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: BoardModel, newItem: BoardModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_board_item, parent, false)

        return BoardViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
package com.example.zelinn.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.ListModel
import com.example.zelinn.ui.board.ListItemViewHolder
import androidx.recyclerview.widget.ListAdapter as AndroidListAdapter

class ListAdapter(private val activity: AppCompatActivity) : AndroidListAdapter<ListModel, ListItemViewHolder>(ListItemCallback()) {
    private class ListItemCallback: DiffUtil.ItemCallback<ListModel>() {
        override fun areContentsTheSame(oldItem: ListModel, newItem: ListModel): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areItemsTheSame(oldItem: ListModel, newItem: ListModel): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_list_item, parent, false)

        return ListItemViewHolder(root!!, activity)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun submitList(list: MutableList<ListModel>?) {
        val addList = ListModel("ADDING", "")
        list?.add(addList)
        super.submitList(list)
    }
}
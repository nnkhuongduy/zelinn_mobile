package com.example.zelinn.ui.board.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.zelinn.R
import com.example.zelinn.classes.ListModel
import androidx.recyclerview.widget.ListAdapter as AndroidListAdapter

class BoardListAdapter() : AndroidListAdapter<ListModel, ListItemViewHolder>(ListItemCallback()) {
    lateinit var addCardCallback: (ListModel) -> Unit
    lateinit var addListCallback: () -> Unit

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

        return ListItemViewHolder(root, addCardCallback, addListCallback)
    }

    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    fun submitLists(list: List<ListModel>, commitCallback: Runnable?) {
        val mutableList = list.toMutableList()
        val addList = ListModel("ADDING", "", null)

        mutableList.add(addList)

        super.submitList(mutableList) {
            commitCallback?.run()
        }
    }
}
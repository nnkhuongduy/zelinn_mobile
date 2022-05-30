package com.example.zelinn.ui.pepper.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.adapters.PepperListItemCallback
import com.example.zelinn.classes.PepperListModel

class PepperListAdapter: ListAdapter<PepperListModel, PepperListViewHolder>(PepperListItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PepperListViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_pepper_list, parent, false)

        return PepperListViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: PepperListViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
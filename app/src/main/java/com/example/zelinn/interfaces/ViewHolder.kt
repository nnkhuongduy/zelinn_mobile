package com.example.zelinn.interfaces

import androidx.recyclerview.widget.RecyclerView

interface ViewHolder<T> {
    fun onBind(model: T): Unit
}
package com.example.zelinn.ui.pepper.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.zelinn.R
import com.example.zelinn.adapters.PepperItemCallback
import com.example.zelinn.classes.PepperModel

class PepperBoardAdapter(): ListAdapter<PepperModel, PepperBoardViewHolder>(PepperItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PepperBoardViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(R.layout.fragment_pepper_board, parent, false)

        return PepperBoardViewHolder(root as ViewGroup)
    }

    override fun onBindViewHolder(holder: PepperBoardViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }
}
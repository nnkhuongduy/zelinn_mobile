package com.example.zelinn.ui.list

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.ListModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.card.CardAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListItemViewHolder(
    itemView: View,
    var addCardCallback: (ListModel) -> Unit,
    var addListCallback: () -> Unit,
    var editListCallback: (ListModel) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private lateinit var nameView: TextView
    private lateinit var layoutView: LinearLayout
    private lateinit var addCardBtnView: ImageButton
    private lateinit var addListBtnView: Button
    private lateinit var rv: RecyclerView
    private lateinit var sortBtn: ImageButton
    private lateinit var sortMenu: CardView
    private lateinit var sortCreated: Button
    private lateinit var sortDue: Button
    private lateinit var sortName: Button
    private lateinit var infoBtn: ImageButton

    private lateinit var list: ListModel

    private val adapter = CardAdapter()
    private var cards: List<CardModel> = emptyList()
    private var sort = "CREATED"
    private var sortDirection = "ASC"

    fun onBind(list: ListModel) {
        this.list = list
        this.cards = list.cards

        findViews()
        populate()
        watch()
    }

    private fun findViews() {
        nameView = itemView.findViewById(R.id.list_name_view)
        layoutView = itemView.findViewById(R.id.list_item_layout)
        addCardBtnView = itemView.findViewById(R.id.list_item_add_card_btn)
        addListBtnView = itemView.findViewById(R.id.list_item_create_btn)
        rv = itemView.findViewById(R.id.list_item_rv)
        sortBtn = itemView.findViewById(R.id.list_item_sort_btn)
        sortMenu = itemView.findViewById(R.id.list_item_sort_menu)
        sortCreated = itemView.findViewById(R.id.list_item_sort_created)
        sortDue = itemView.findViewById(R.id.list_item_sort_due)
        sortName = itemView.findViewById(R.id.list_item_sort_name)
        infoBtn = itemView.findViewById(R.id.list_item_info_btn)
    }

    private fun populate() {
        nameView.text = list.name

        layoutView.visibility = if (list.id == "ADDING") View.GONE else View.VISIBLE
        rv.visibility = if (list.id == "ADDING") View.GONE else View.VISIBLE
        addCardBtnView.visibility = if (list.id == "ADDING") View.GONE else View.VISIBLE
        addListBtnView.visibility = if (list.id != "ADDING") View.GONE else View.VISIBLE

        if (list.id != "ADDING") {
            rv.layoutManager = LinearLayoutManager(itemView.context)
            rv.adapter = adapter

            adapter.apply {
                submitList(list.cards)
            }
        }
    }

    private fun watch() {
        addCardBtnView.setOnClickListener {
            addCardCallback(list)
        }
        addListBtnView.setOnClickListener {
            addListCallback()
        }
        sortBtn.setOnClickListener {
            sortMenu.visibility =
                if (sortMenu.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        sortCreated.setOnClickListener {
            sortDirection = if (sort == "CREATED" && sortDirection == "ASC") "DESC" else "ASC"
            sort = "CREATED"
            sort()
        }
        sortDue.setOnClickListener {
            sortDirection = if (sort == "DUE" && sortDirection == "ASC") "DESC" else "ASC"
            sort = "DUE"
            sort()
        }
        sortName.setOnClickListener {
            sortDirection = if (sort == "NAME" && sortDirection == "ASC") "DESC" else "ASC"
            sort = "NAME"
            sort()
        }
        infoBtn.setOnClickListener {
            editListCallback(list)
        }
    }

    private fun sort() {
        sortCreated.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        sortName.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        sortDue.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        sortCreated.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_light))
        sortName.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_light))
        sortDue.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.background_light))

        var drawable = 0
        var color = ContextCompat.getColor(itemView.context, R.color.primary)
        var button: Button? = null

        if (sortDirection == "ASC") {
            drawable = R.drawable.ic_arrow_upward

            when (sort) {
                "CREATED" -> {
                    button = sortCreated
                    cards = cards.sortedBy { it.createdAt }
                }
                "DUE" -> {
                    button = sortDue
                    cards = cards.sortedBy { it.due }
                }
                "NAME" -> {
                    button = sortName
                    cards = cards.sortedBy { it.name }
                }
            }
        }
        else {
            drawable = R.drawable.ic_arrow_downward
            when (sort) {
                "CREATED" -> {
                    button = sortCreated
                    cards = cards.sortedByDescending { it.createdAt }
                }
                "DUE" -> {
                    button = sortDue
                    cards = cards.sortedByDescending { it.due }
                }
                "NAME" -> {
                    button = sortName
                    cards = cards.sortedByDescending { it.name }
                }
            }
        }

        button?.setBackgroundColor(color)
        button?.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawable, 0)

        adapter.apply {
            submitList(cards)
        }
    }
}
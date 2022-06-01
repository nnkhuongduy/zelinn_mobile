package com.example.zelinn

import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.ListModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.interfaces.CompleteCardBody
import com.example.zelinn.ui.card.CardViewModel
import com.example.zelinn.ui.card.ParticipantAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class CardActivity: AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var avaRView: RecyclerView
    private lateinit var nameText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var listText: TextView
    private lateinit var startDateText: TextView
    private lateinit var startTimeText: TextView
    private lateinit var dueDateText: TextView
    private lateinit var dueTimeText: TextView
    private lateinit var actionBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var actionCheckedBtn: Button

    private val model: CardViewModel by viewModels()
    private val adapter = ParticipantAdapter()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        findViews()
        init()
        watch()
    }

    private fun findViews() {
        this.imageView = findViewById(R.id.card_activity_image)
        this.avaRView = findViewById(R.id.card_activity_avatar_rv)
        this.nameText = findViewById(R.id.card_activity_name)
        this.listText = findViewById(R.id.card_activity_list)
        this.descriptionText = findViewById(R.id.card_activity_description)
        this.startDateText = findViewById(R.id.card_activity_start_date_text)
        this.startTimeText = findViewById(R.id.card_activity_start_time_text)
        this.dueDateText = findViewById(R.id.card_activity_due_date_text)
        this.dueTimeText = findViewById(R.id.card_activity_due_time_text)
        this.actionBtn = findViewById(R.id.card_activity_action_btn)
        this.deleteBtn = findViewById(R.id.card_activity_delete_btn)
        this.actionCheckedBtn = findViewById(R.id.card_activity_action_btn_checked)
    }

    private fun init() {
        getCard()

        avaRView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        avaRView.adapter = adapter
    }

    private fun watch() {
        model.card.observe(this) {
            populate(it)

            getList()
        }
        model.list.observe(this) {
            listText.text = it.name
        }
        actionBtn.setOnClickListener {
            completeCard()
        }
        actionCheckedBtn.setOnClickListener {
            completeCard()
        }
        deleteBtn.setOnClickListener {
            showDeleteDialog()
        }
    }

    private fun populate(card: CardModel) {
        adapter.apply {
            submitList(card.participants)
        }

        Glide.with(this).load(card.thumbnail).into(imageView)

        nameText.text = card.name
        descriptionText.text = card.description

        startDateText.text = SimpleDateFormat("d'/'M").format(card.start)
        startTimeText.text = SimpleDateFormat("H':'m a").format(card.start)

        dueDateText.text = SimpleDateFormat("d'/'M").format(card.due)
        dueTimeText.text = SimpleDateFormat("H':'m a").format(card.due)

        actionBtn.visibility = if (card.completed) View.GONE else View.VISIBLE
        actionCheckedBtn.visibility = if (card.completed) View.VISIBLE else View.GONE
    }

    private fun getCard() {
        val bundle = intent.extras
        val card = bundle?.get("card") as CardModel

        RetrofitInstance.retrofit.getCard(card.id).enqueue(object : Callback<CardModel> {
            override fun onResponse(call: Call<CardModel>, response: Response<CardModel>) {
                val card = response.body()

                if (response.isSuccessful && card != null) {
                    model.setCard(card)
                }
            }

            override fun onFailure(call: Call<CardModel>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }

    private fun getList() {
        val card = model.card.value ?: return

        RetrofitInstance.retrofit.getList(null, card.id).enqueue(object : Callback<ListModel> {
            override fun onResponse(call: Call<ListModel>, response: Response<ListModel>) {
                val list = response.body()

                if (response.isSuccessful && list != null) {
                    model.setList(list)
                }
            }

            override fun onFailure(call: Call<ListModel>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }

    private fun completeCard() {
        val card = model.card.value ?: return
        val body = CompleteCardBody(card.id)

        RetrofitInstance.retrofit.completeCard(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getCard()
                    ZelinnApp.prefs.push(getString(R.string.preference_pepper_flag), true)
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }

    private fun deleteCard() {
        val card = model.card.value ?: return

        RetrofitInstance.retrofit.deleteCard(card.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_pepper_flag), true)
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)

                    onBackPressed()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }

    private fun showDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(this).setView(R.layout.dialog_confirmation)
        val dialog = builder.show()

        val thumbnailView = dialog.findViewById<CardView>(R.id.dialog_confirmation_thumbnail_card)!!
        val titleText = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val descriptionText = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!

        thumbnailView.visibility = View.GONE
        descriptionText.visibility = View.GONE

        titleText.text = getString(R.string.card_delete_confirmation)

        cancelBtn.setOnClickListener { dialog.dismiss() }
        confirmBtn.setOnClickListener {
            deleteCard()
            dialog.dismiss()
        }
    }
}
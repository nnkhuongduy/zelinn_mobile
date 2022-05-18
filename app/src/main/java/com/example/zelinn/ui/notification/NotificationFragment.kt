package com.example.zelinn.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.NotificationModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentNotificationBinding
import com.example.zelinn.interfaces.ConfirmBoardInvitationBody
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.ak1.BubbleTabBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment: Fragment() {
    private lateinit var swipeLayout : SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentNotificationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = NotificationAdapter()
    private val model: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        swipeLayout = root.findViewById(R.id.notification_swipe_refresh)
        recyclerView = root.findViewById(R.id.notification_recycler)

        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_notification, false)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.clickCallback = { notification ->
            if (notification.type == getString(R.string.notification_type_invite_board)) {
                if (!notification.seen)
                    createConfirmInvitationDialog(notification)
            }
            else markRead(notification)
        }

        swipeLayout.setOnRefreshListener {
            getNotifications()
        }
        model.notifications.observe(viewLifecycleOwner) {
            adapter.apply {
                submitList(it)
            }
        }
        getNotifications()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getNotifications() {
        RetrofitInstance.retrofit.getNotifications().enqueue(object:
            Callback<List<NotificationModel>> {
            override fun onResponse(
                call: Call<List<NotificationModel>>,
                response: Response<List<NotificationModel>>
            ) {
                val notifications = response.body()

                if (response.isSuccessful && notifications != null) {
                    model.setNotifications(notifications)
                    swipeLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<NotificationModel>>, t: Throwable) {}
        })
    }

    private fun markRead(notification: NotificationModel) {

    }

    private fun createConfirmInvitationDialog(notification: NotificationModel) {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_confirmation)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val descriptionView = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val thumbnailView = dialog.findViewById<ImageView>(R.id.dialog_confirmation_thumbnail)!!
        val thumbnailCard = dialog.findViewById<CardView>(R.id.dialog_confirmation_thumbnail_card)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!

        thumbnailCard.visibility = View.VISIBLE
        titleView.text = notification.title
        descriptionView.text = notification.description
        Glide.with(this).load(notification.icon).into(thumbnailView)

        confirmBtn.setOnClickListener {
            confirmBtn.isEnabled = false
            cancelBtn.isEnabled = false
            confirmInvitation(notification, true, dialog)
        }
        cancelBtn.setOnClickListener {
            confirmBtn.isEnabled = false
            cancelBtn.isEnabled = false
            confirmInvitation(notification, false, dialog)
        }
    }

    private fun confirmInvitation(notification: NotificationModel, result: Boolean, dialog: AlertDialog) {
        val body = ConfirmBoardInvitationBody(notification.id, result)
        RetrofitInstance.retrofit.confirmBoardInvitation(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                dialog.dismiss()

                if (response.isSuccessful) {
                    getNotifications()
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                dialog.dismiss()
            }
        })
    }
}
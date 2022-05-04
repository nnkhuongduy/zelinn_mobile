package com.example.zelinn.ui.board.member_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardMemberListBinding
import com.example.zelinn.interfaces.RemoveMembersBody
import com.example.zelinn.ui.board.BoardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardMemberListFragment : Fragment() {
    private lateinit var removeBtn: Button
    private lateinit var closeBtn: Button
    private lateinit var joinedBtn: Button
    private lateinit var pendingBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchText: EditText
    private lateinit var dialog: AlertDialog

    private var _binding: FragmentBoardMemberListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: BoardViewModel by activityViewModels()
    private val adapter = BoardMemberListAdapter()
    private val selectedMembers: MutableList<MemberModel> = mutableListOf()
    private var isOwner: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardMemberListBinding.inflate(inflater, container, false)
        val root = binding.root

        removeBtn = root.findViewById(R.id.board_menu_member_list_remove)
        closeBtn = root.findViewById(R.id.board_menu_member_list_close)
        joinedBtn = root.findViewById(R.id.board_menu_member_list_joined)
        pendingBtn = root.findViewById(R.id.board_menu_member_list_pending)
        recyclerView = root.findViewById(R.id.board_menu_member_list_rv)
        searchText = root.findViewById(R.id.board_menu_member_list_search)

        model.board.observe(viewLifecycleOwner) {
            isOwner =
                it.owner.id == Hawk.get<UserModel>(getString(R.string.preference_current_user)).id
            adapter.selectable = isOwner
            removeBtn.visibility = if (isOwner) View.VISIBLE else View.GONE

            populate()
        }
        model.memberListState.observe(viewLifecycleOwner) {
            populate()
        }
        model.memberListQuery.observe(viewLifecycleOwner) {
            populate()
        }

        joinedBtn.setOnClickListener {
            model.setMemberListState(true)
        }
        pendingBtn.setOnClickListener {
            model.setMemberListState(false)
        }
        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        removeBtn.setOnClickListener {
            if (isOwner && selectedMembers.size > 0) {
                createConfirmDialog()
            }
        }
        searchText.doOnTextChanged { text, start, before, count ->
            model.setMemberListQuery(text.toString())
        }

        adapter.onItemClickListener = { member, state ->
            if (state) {
                selectedMembers.add(member)
            } else {
                selectedMembers.remove(member)
            }

            if (isOwner) checkState()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populate() {
        val state = model.memberListState.value

        if (state == true) {
            joinedBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            pendingBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.background_surface
                )
            )
            populateJoined()
        } else {
            joinedBtn.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.background_surface
                )
            )
            pendingBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
            populatePending()
        }
    }

    private fun populateJoined() {
        val board = model.board.value!!
        val query = model.memberListQuery.value ?: ""

        adapter.apply {
            submitList(board.members.filter { it.name.contains(query) || it.email.contains(query) })
        }
    }

    private fun populatePending() {
        val board = model.board.value!!
        val query = model.memberListQuery.value ?: ""

        adapter.apply {
            submitList(board.pending.filter { it.name.contains(query) || it.email.contains(query) })
        }
    }

    private fun checkState() {
        removeBtn.isEnabled = selectedMembers.size > 0
        removeBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), if (selectedMembers.size > 0) R.color.danger else R.color.background_surface))
    }

    private fun createConfirmDialog() {
        val builder =
            MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_confirmation)
        dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val descriptionView = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!
        val cardView = dialog.findViewById<CardView>(R.id.dialog_confirmation_thumbnail_card)!!

        titleView.text = getString(R.string.board_menu_member_list_remove_confirm)
        descriptionView.visibility = View.GONE
        cardView.visibility = View.GONE

        confirmBtn.setOnClickListener {
            confirmBtn.isEnabled = false
            cancelBtn.isEnabled = false
            removeMembers()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun removeMembers() {
        val body = RemoveMembersBody(model.board.value!!.id, selectedMembers.map { it.id })

        RetrofitInstance.retrofit.removeMembers(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                dialog.dismiss()
                selectedMembers.clear()
                checkState()

                if (response.isSuccessful) {
                    model.resetBoard()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                dialog.dismiss()
                selectedMembers.clear()
                checkState()
            }

        })
    }
}
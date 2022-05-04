package com.example.zelinn.ui.board.invite

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.MemberModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentBoardInviteBinding
import com.example.zelinn.interfaces.PostInviteBoardBody
import com.example.zelinn.ui.board.BoardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardMenuInviteFragment: Fragment() {
    private lateinit var searchText: EditText
    private lateinit var searchBtn: Button
    private lateinit var progressView: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var actionBtn: Button
    private lateinit var actionProgressView: ProgressBar
    private var _binding: FragmentBoardInviteBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = BoardMenuInviteAdapter()
    private val model: BoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardInviteBinding.inflate(inflater, container, false)
        val root = binding.root

        val backBtn = root.findViewById<Button>(R.id.board_menu_invite_close)
        searchText = root.findViewById(R.id.board_menu_invite_search)
        searchBtn = root.findViewById(R.id.board_menu_invite_search_btn)
        progressView = root.findViewById(R.id.board_menu_invite_progress)
        recyclerView = root.findViewById(R.id.board_menu_invite_recycle)
        actionBtn = root.findViewById(R.id.board_menu_invite_save_btn)
        actionProgressView = root.findViewById(R.id.board_menu_invite_save_progress)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        backBtn.setOnClickListener { requireActivity().onBackPressed() }
        searchBtn.setOnClickListener { queryUsers() }
        searchText.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                queryUsers()
                return@setOnKeyListener true
            }

            return@setOnKeyListener false
        }
        actionBtn.setOnClickListener { inviteMembers() }
        adapter.onItemClickListener = { member, state ->
            if (state) {
                model.addMember(member)
            } else {
                model.removeMember(member)
            }
        }
        model.resetSelectedMembers()
        model.selectedMembers.observe(viewLifecycleOwner) {
            actionBtn.isEnabled = it.size > 0
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toggleSearchBtnState() {
        if (searchBtn.isEnabled) {
            searchBtn.isEnabled = false
            progressView.visibility = View.VISIBLE
        } else {
            searchBtn.isEnabled = true
            progressView.visibility = View.GONE
        }
    }

    private fun toggleActionBtnState() {
        if (actionBtn.isEnabled) {
            actionBtn.isEnabled = false
            actionBtn.text = ""
            actionProgressView.visibility = View.VISIBLE
        } else {
            actionBtn.isEnabled = true
            actionBtn.text = getString(R.string.common_invite)
            actionProgressView.visibility = View.GONE
        }
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)

        titleView?.text = getString(R.string.board_menu_invite_success)

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        title.text = getString(R.string.board_menu_invite_failed)
    }

    private fun queryUsers() {
        val value = searchText.text.toString()
        toggleSearchBtnState()

        RetrofitInstance.retrofit.queryMembersToInvite(model.board.value!!.id, value).enqueue(object: Callback<List<MemberModel>> {
            override fun onResponse(
                call: Call<List<MemberModel>>,
                response: Response<List<MemberModel>>
            ) {
                val users = response.body()
                toggleSearchBtnState()

                if (response.isSuccessful && users != null) {
                    adapter.apply {
                        submitList(users)
                    }
                }
            }

            override fun onFailure(call: Call<List<MemberModel>>, t: Throwable) {
                toggleSearchBtnState()
            }
        })
    }

    private fun inviteMembers() {
        toggleActionBtnState()
        val body = PostInviteBoardBody(model.board.value!!.id, model.selectedMembers.value!!.map { it.id })

        RetrofitInstance.retrofit.inviteToBoard(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                toggleActionBtnState()

                if (response.isSuccessful) {
                    model.resetBoard()
                    showSuccessDialog()
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                toggleActionBtnState()
                showErrorDialog()
            }
        })
    }
}
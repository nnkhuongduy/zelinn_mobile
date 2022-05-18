package com.example.zelinn.ui.board.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardOwnerBinding
import com.example.zelinn.ui.board.BoardViewModel
import com.google.gson.Gson

class BoardMenuOwnerFragment: Fragment() {
    private lateinit var closeBtn: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var actionBtn: Button
    private lateinit var searchText: EditText

    private var _binding: FragmentBoardOwnerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = BoardMenuOwnerAdapter()
    private val model: BoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardOwnerBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()

        closeBtn.setOnClickListener { requireActivity().onBackPressed() }
        actionBtn.setOnClickListener {
            model.updateOwner()
            requireActivity().onBackPressed()
        }
        searchText.doOnTextChanged { text, start, before, count ->
            populate(text.toString())
        }
        adapter.owner = ZelinnApp.prefs.pull(getString(R.string.preference_current_user))
        adapter.itemClickCallback = {member ->
            val owner = UserModel(member.id, member.email, member.name, "", member.avatar, emptyList())
            model.setTempOwner(owner)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        populate(null)

        watch()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.board_menu_owner_close)
        recyclerView = binding.root.findViewById(R.id.board_menu_owner_rv)
        actionBtn = binding.root.findViewById(R.id.board_menu_owner_action)
        searchText = binding.root.findViewById(R.id.board_menu_owner_search)
    }

    private fun populate(query: String?) {
        val board = model.board.value!!

        adapter.apply {
            submitList(board.members.filter {
                val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
                return@filter it.id != user.id &&
                (it.name.contains(query ?: "") || it.email.contains(query ?: ""))
            })
        }
    }

    private fun watch() {
        model.owner.observe(viewLifecycleOwner) {
            adapter.owner = it
            adapter.notifyDataSetChanged()

            actionBtn.isEnabled = it != null
        }
    }
}
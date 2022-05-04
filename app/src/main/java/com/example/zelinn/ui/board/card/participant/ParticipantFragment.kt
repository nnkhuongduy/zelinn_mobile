package com.example.zelinn.ui.board.card.participant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentCreateCardParticipantBinding
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.board.card.CardViewModel

class ParticipantFragment : Fragment() {
    private lateinit var closeBtn: Button
    private lateinit var searchText: EditText
    private lateinit var actionBtn: Button
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentCreateCardParticipantBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val boardModel: BoardViewModel by activityViewModels()
    private val cardModel: CardViewModel by activityViewModels()
    private val adapter = ParticipantAdapter()

    private var order = 0;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCardParticipantBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()
        populate()
        watch()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.create_card_participant_close)
        searchText = binding.root.findViewById(R.id.create_card_participant_search)
        actionBtn = binding.root.findViewById(R.id.create_card_participant_action)
        recyclerView = binding.root.findViewById(R.id.create_card_participant_rv)
    }

    private fun populate() {
        adapter.setOnItemClickListener { participant, index ->
            this.order++
            participant.selected = !participant.selected
            participant.order = this.order
            adapter.notifyItemChanged(index)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val board = boardModel.board.value!!
        val participants = cardModel.participants.value!!

        adapter.apply {
            submitList(board.members.map { it.selected = participants.contains(it); it })
        }
    }

    private fun watch() {
        actionBtn.setOnClickListener {
            cardModel.setParticipants(adapter.currentList.filter { it.selected }.sortedBy { it.order })

            requireActivity().onBackPressed()

        }
        closeBtn.setOnClickListener { requireActivity().onBackPressed() }
    }
}
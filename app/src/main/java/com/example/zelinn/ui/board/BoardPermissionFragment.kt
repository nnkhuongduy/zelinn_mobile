package com.example.zelinn.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentBoardPermissionBinding
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.boards.BoardsViewModel
import com.google.android.material.card.MaterialCardView

class BoardPermissionFragment: Fragment() {
    private var _binding: FragmentBoardPermissionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val boardsViewModel: BoardsViewModel by activityViewModels()
    private val boardViewModel: BoardViewModel by activityViewModels()

    companion object {
        const val TAG = "BOARD_PERMISSION"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardPermissionBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_board_permission_close_btn)
        val publicView = root.findViewById<MaterialCardView>(R.id.create_board_permission_public_view)
        val privateView = root.findViewById<MaterialCardView>(R.id.create_board_permission_private_view)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        publicView.setOnClickListener {
            boardsViewModel.setBoardPermission(getString(R.string.board_public_value))
            boardViewModel.setUpdatePermission(getString(R.string.board_public_value))
        }
        privateView.setOnClickListener {
            boardsViewModel.setBoardPermission(getString(R.string.board_private_value))
            boardViewModel.setUpdatePermission(getString(R.string.board_private_value))
        }

        boardsViewModel.boardPermission.observe(viewLifecycleOwner) {
            if (it == getString(R.string.board_public_value)) {
                selectPermission(R.id.create_board_permission_public_view)
                unselectPermission(R.id.create_board_permission_private_view)
            } else {
                selectPermission(R.id.create_board_permission_private_view)
                unselectPermission(R.id.create_board_permission_public_view)
            }
        }
        boardViewModel.updateBoard.observe(viewLifecycleOwner) {
            if (it.permission == getString(R.string.board_public_value)) {
                selectPermission(R.id.create_board_permission_public_view)
                unselectPermission(R.id.create_board_permission_private_view)
            } else {
                selectPermission(R.id.create_board_permission_private_view)
                unselectPermission(R.id.create_board_permission_public_view)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectPermission(id: Int) {
        val permissionView = binding.root.findViewById<MaterialCardView>(id)

        permissionView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.background))
        permissionView.strokeColor = ContextCompat.getColor(binding.root.context, R.color.primary)
    }

    private fun unselectPermission(id: Int) {
        val permissionView = binding.root.findViewById<MaterialCardView>(id)

        permissionView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.background_surface))
        permissionView.strokeColor = ContextCompat.getColor(binding.root.context, android.R.color.transparent)
    }
}
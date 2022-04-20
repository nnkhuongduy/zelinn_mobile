package com.example.zelinn.ui.board_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentCreateBoardPermissionBinding
import com.google.android.material.card.MaterialCardView

class CreateBoardPermissionFragment: Fragment() {
    companion object {
        fun newInstance() = CreateBoardPermissionFragment()
    }

    private var _binding: FragmentCreateBoardPermissionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBoardPermissionBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_board_permission_close_btn)
        val publicView = root.findViewById<MaterialCardView>(R.id.create_board_permission_public_view)
        val privateView = root.findViewById<MaterialCardView>(R.id.create_board_permission_private_view)

        selectPermission(R.id.create_board_permission_public_view)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        publicView.setOnClickListener {
            selectPermission(R.id.create_board_permission_public_view)
            unselectPermission(R.id.create_board_permission_private_view)
        }
        privateView.setOnClickListener {
            selectPermission(R.id.create_board_permission_private_view)
            unselectPermission(R.id.create_board_permission_public_view)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val permissionBtn = activity?.findViewById<Button>(R.id.create_board_permission_btn)

        permissionBtn?.setOnClickListener {
            val permissionFragment = CreateBoardPermissionFragment.newInstance()
            activity?.supportFragmentManager?.beginTransaction()
                ?.setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                )
                ?.replace(R.id.board_list_create_board_fragment, permissionFragment, "CREATE_BOARD_PERMISSION")
                ?.addToBackStack("CREATE_BOARD_PERMISSION")
                ?.commit()
        }
    }

    fun selectPermission(id: Int) {
        val permissionView = binding.root.findViewById<MaterialCardView>(id)

        permissionView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.background))
        permissionView.strokeColor = ContextCompat.getColor(binding.root.context, R.color.primary)
    }

    fun unselectPermission(id: Int) {
        val permissionView = binding.root.findViewById<MaterialCardView>(id)

        permissionView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.background_surface))
        permissionView.strokeColor = ContextCompat.getColor(binding.root.context, android.R.color.transparent)
    }
}
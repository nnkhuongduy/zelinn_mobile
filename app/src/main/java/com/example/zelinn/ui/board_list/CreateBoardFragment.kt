package com.example.zelinn.ui.board_list

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentCreateBoardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CreateBoardFragment : Fragment() {

    companion object {
        fun newInstance() = CreateBoardFragment()
    }

    private lateinit var boardNameInput: TextInputEditText

    private var _binding: FragmentCreateBoardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBoardBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_board_close_btn)
        val createBtn = root.findViewById<Button>(R.id.create_board_create_btn)
        boardNameInput = root.findViewById<TextInputEditText>(R.id.create_board_name_input)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            createDialog()
        }
        boardNameInput.doOnTextChanged { text, start, before, count ->
            createBtn.isEnabled = isValid()
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

    private fun isValid(): Boolean {
        return boardNameInput.text.toString().isNotEmpty()
    }

    private fun createDialog() {
        val builder = MaterialAlertDialogBuilder(binding.root.context).setView(R.layout.dialog_board_create_success)
        val dialog = builder.show()

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }
}
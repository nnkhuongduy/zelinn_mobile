package com.example.zelinn.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentCreateListBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CreateListFragment: Fragment() {
    private lateinit var listNameInput: TextInputEditText

    private var _binding: FragmentCreateListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CreateListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateListBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_list_close_btn)
        val createBtn = root.findViewById<Button>(R.id.create_list_create_btn)
        listNameInput = root.findViewById(R.id.create_list_name_input)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            createDialog()
        }
        listNameInput.doOnTextChanged { text, start, before, count ->
            createBtn.isEnabled = isValid()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValid(): Boolean {
        return listNameInput.text.toString().isNotEmpty()
    }

    private fun createDialog() {
        val builder = MaterialAlertDialogBuilder(binding.root.context).setView(R.layout.dialog_list_create_success)
        val dialog = builder.show()

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }
}
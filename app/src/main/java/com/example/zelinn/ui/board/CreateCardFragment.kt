package com.example.zelinn.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.zelinn.R
import com.example.zelinn.databinding.FragmentCreateCardBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class CreateCardFragment: Fragment() {
    private lateinit var cardNameInput: TextInputEditText
    private lateinit var cardDescriptionInput: TextInputEditText

    private var _binding: FragmentCreateCardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CreateCardFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateCardBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_card_close_btn)
        val createBtn = root.findViewById<Button>(R.id.create_card_create_btn)
        cardNameInput = root.findViewById(R.id.create_card_name_input)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            createDialog()
        }
        cardNameInput.doOnTextChanged { text, start, before, count ->
            createBtn.isEnabled = isValid()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValid(): Boolean {
        return cardNameInput.text.toString().isNotEmpty()
    }

    private fun createDialog() {
        val builder = MaterialAlertDialogBuilder(binding.root.context).setView(R.layout.dialog_card_create_success)
        val dialog = builder.show()

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }
}
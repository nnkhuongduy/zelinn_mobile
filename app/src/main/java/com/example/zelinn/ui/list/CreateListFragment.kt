package com.example.zelinn.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentCreateListBinding
import com.example.zelinn.interfaces.CreateListBody
import com.example.zelinn.ui.board.BoardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateListFragment: Fragment() {
    private lateinit var nameInput: EditText
    private lateinit var priorityInput: EditText
    private lateinit var closeBtn: Button
    private lateinit var createBtn: Button
    private lateinit var progressView: ProgressBar

    private var _binding: FragmentCreateListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: BoardViewModel by activityViewModels()
    private var valid = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateListBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            if (valid) {
                createList()
            }
        }
        nameInput.doAfterTextChanged {
            updateValidState()
        }
        priorityInput.doAfterTextChanged {
            updateValidState()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.create_list_close_btn)
        createBtn = binding.root.findViewById(R.id.create_list_create_btn)
        nameInput = binding.root.findViewById(R.id.create_list_name_input)
        priorityInput = binding.root.findViewById(R.id.create_list_priority_input)
        progressView = binding.root.findViewById(R.id.create_list_progress)
    }

    private fun updateValidState() {
        valid = nameInput.text.toString().isNotEmpty() && priorityInput.text.toString().isNotEmpty() && priorityInput.text.toString().toInt() > 0

        createBtn.isEnabled = valid
    }

    private fun toggleLoadingState() {
        val isLoading = progressView.visibility == View.VISIBLE

        progressView.visibility = if (isLoading) View.GONE else View.VISIBLE
        createBtn.isEnabled = !isLoading
        createBtn.text = if (isLoading) getString(R.string.create_action) else ""
        nameInput.isEnabled = !isLoading
        priorityInput.isEnabled = !isLoading
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(binding.root.context).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)!!

        titleView.text = getString(R.string.create_list_success)

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        titleView.text = getString(R.string.create_list_failed)
    }

    private fun createList() {
        val board = model.board.value ?: return
        val name = nameInput.text.toString()
        val priority = priorityInput.text.toString().toInt()
        val body = CreateListBody(board.id, name, priority)

        toggleLoadingState()

        RetrofitInstance.retrofit.createList(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                toggleLoadingState()

                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                    showSuccessDialog()
                    model.resetList()
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                toggleLoadingState()
                showErrorDialog()
            }
        })
    }
}
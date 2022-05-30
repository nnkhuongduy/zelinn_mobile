package com.example.zelinn.ui.list.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentListEditBinding
import com.example.zelinn.interfaces.UpdateListBody
import com.example.zelinn.ui.board.BoardViewModel
import com.example.zelinn.ui.list.ListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListEditFragment: Fragment() {
    private lateinit var closeBtn: Button
    private lateinit var titleText: TextView
    private lateinit var saveBtn: Button
    private lateinit var nameText: TextInputEditText
    private lateinit var priorityText: TextInputEditText
    private lateinit var deleteBtn: Button

    private var _binding: FragmentListEditBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val listModel: ListViewModel by activityViewModels()
    private val boardModel: BoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListEditBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()
        init()
        watch()

        return root
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.list_edit_close_btn)
        titleText = binding.root.findViewById(R.id.list_edit_title)
        saveBtn = binding.root.findViewById(R.id.list_edit_save_btn)
        nameText = binding.root.findViewById(R.id.list_edit_name_input)
        priorityText = binding.root.findViewById(R.id.list_edit_priority_input)
        deleteBtn = binding.root.findViewById(R.id.list_edit_delete_btn)
    }

    private fun init() {
        val list = listModel.list.value ?: return

        titleText.text = list.name
        nameText.setText(list.name)
        priorityText.setText(list.priority.toString())
    }

    private fun watch() {
        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        nameText.doAfterTextChanged {
            checkValid()
        }
        priorityText.doAfterTextChanged {
            checkValid()
        }
        deleteBtn.setOnClickListener { showDeleteDialog() }
        saveBtn.setOnClickListener { saveList() }
    }

    private fun checkValid() {
        val valid = !nameText.text.isNullOrEmpty() && !priorityText.text.isNullOrEmpty()

        saveBtn.isEnabled = valid
    }

    private fun showDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_confirmation)
        val dialog = builder.show()

        val thumbnailView = dialog.findViewById<CardView>(R.id.dialog_confirmation_thumbnail_card)!!
        val titleText = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val descriptionText = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!

        thumbnailView.visibility = View.GONE
        titleText.text = getString(R.string.list_delete_confirmation)
        descriptionText.text = getString(R.string.list_delete_subtitle)

        cancelBtn.setOnClickListener { dialog.dismiss() }
        confirmBtn.setOnClickListener { dialog.dismiss(); deleteList()  }
    }

    private fun deleteList() {
        val list = listModel.list.value ?: return

        RetrofitInstance.retrofit.deleteList(list.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                    boardModel.resetList()
                    requireActivity().onBackPressed()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }

    private fun saveList() {
        val list = listModel.list.value ?: return
        val body = UpdateListBody(list.id, nameText.text.toString(), priorityText.text.toString().toInt())

        RetrofitInstance.retrofit.updateList(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                    boardModel.resetList()
                    requireActivity().onBackPressed()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }
}
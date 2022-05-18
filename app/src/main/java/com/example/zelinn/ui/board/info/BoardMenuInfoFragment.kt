package com.example.zelinn.ui.board.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardInfoBinding
import com.example.zelinn.interfaces.PostUpdateBoardBody
import com.example.zelinn.ui.board.BoardViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardMenuInfoFragment: Fragment() {
    private lateinit var closeBtn: Button
    private lateinit var avatarView: ImageView
    private lateinit var nameView: TextView
    private lateinit var descriptionText: EditText
    private lateinit var saveBtn: Button
    private lateinit var ownerBtn: CardView
    private lateinit var arrowView: ImageView

    private var _binding: FragmentBoardInfoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: BoardViewModel by activityViewModels()
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeOwnerFragment()
        }
    }
    private var isOwner = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardInfoBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()

        populate()

        watchModel()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.board_menu_info_close)
        avatarView = binding.root.findViewById(R.id.board_menu_info_owner_avatar)
        nameView = binding.root.findViewById(R.id.board_menu_info_owner_name)
        descriptionText = binding.root.findViewById(R.id.board_menu_info_description)
        saveBtn = binding.root.findViewById(R.id.board_menu_info_save)
        ownerBtn = binding.root.findViewById(R.id.board_menu_info_owner_btn)
        arrowView = binding.root.findViewById(R.id.board_menu_info_arrow)
    }

    private fun populate() {
        val board = model.updateBoard.value ?: return
        val owner = board.owner
        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)

        if (owner.avatar != null) Glide.with(requireActivity()).load(owner.avatar).into(avatarView)
        else avatarView.setImageResource(R.drawable.ic_person)

        isOwner = model.board.value!!.owner.id == user.id

        arrowView.visibility = if (isOwner) View.VISIBLE else View.GONE

        nameView.text = owner.name
        descriptionText.setText(board.description)
        descriptionText.isEnabled = isOwner
        saveBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
    }

    private fun watchModel() {
        model.updateChanged.observe(viewLifecycleOwner) {
            saveBtn.isEnabled = it
        }

        closeBtn.setOnClickListener { requireActivity().onBackPressed() }
        ownerBtn.setOnClickListener { if (isOwner) createOwnerFragment() }
        saveBtn.setOnClickListener { updateBoard() }
        descriptionText.doOnTextChanged { text, start, before, count ->
            model.setUpdateDescription(text.toString())
        }
    }

    private fun createOwnerFragment() {
        val activity = requireActivity()

        activity.onBackPressedDispatcher.addCallback(backPressedCallback)
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, BoardMenuOwnerFragment(), tag)
            .addToBackStack("BOARD_OWNER")
            .commit()
    }

    private fun closeOwnerFragment() {
        val activity = requireActivity()

        backPressedCallback.remove()
        activity.supportFragmentManager.popBackStack("BOARD_OWNER", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_success_title)!!

        title.text = getString(R.string.board_menu_update_success)

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        title.text = getString(R.string.board_menu_update_failed)
    }

    private fun updateBoard() {
        val board = model.updateBoard.value ?: return
        val body = PostUpdateBoardBody(board.id, board.name, board.thumbnail, board.permission, board.owner.id, board.description)

        RetrofitInstance.retrofit.postUpdateBoard(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)
                    model.resetBoard()
                    showSuccessDialog()
                } else showErrorDialog()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog()
            }

        })
    }
}
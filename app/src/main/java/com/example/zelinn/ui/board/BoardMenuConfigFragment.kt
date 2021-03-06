package com.example.zelinn.ui.board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardConfigBinding
import com.example.zelinn.interfaces.PostUpdateBoardBody
import com.example.zelinn.interfaces.UploadBoardThumbnailResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardMenuConfigFragment: Fragment() {
    private lateinit var thumbnailView: ImageView
    private lateinit var nameText: EditText
    private lateinit var permissionBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var progressView: ProgressBar
    private lateinit var deleteBtn: Button

    private var _binding: FragmentBoardConfigBinding? = null
    private var isOwner = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: BoardViewModel by activityViewModels()
    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closePermissionFragment()
        }
    }
    private val imageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null)
                model.setUpdateThumbnail(it.data!!.data!!)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardConfigBinding.inflate(inflater, container, false)
        val root = binding.root

        val closeBtn = root.findViewById<Button>(R.id.board_menu_config_close)
        thumbnailView = root.findViewById(R.id.board_menu_config_thumbnail_view)
        nameText = root.findViewById(R.id.board_menu_config_name_text)
        permissionBtn = root.findViewById(R.id.board_menu_config_permission_btn)
        saveBtn = root.findViewById(R.id.board_menu_config_save_btn)
        progressView = root.findViewById(R.id.board_menu_config_progress)
        deleteBtn = root.findViewById(R.id.board_menu_config_delete_btn)

        populate()

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        nameText.doOnTextChanged { text, start, before, count ->
            model.setUpdateName(text.toString())
        }
        permissionBtn.setOnClickListener { createPermissionFragment() }
        thumbnailView.setOnClickListener {
            if (isOwner) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                imageLauncher.launch(photoPickerIntent)
            }
        }
        saveBtn.setOnClickListener { onUpdate() }

        nameText.setText(model.updateBoard.value?.name)
        model.updateBoard.observe(viewLifecycleOwner) {
            if (model.updateUri.value == null)
                Glide.with(this).load(it.thumbnail).into(thumbnailView)
            permissionBtn.text = if (it.permission == getString(R.string.board_public_value)) getString(R.string.permission_public) else getString(R.string.permission_private)

        }
        model.updateUri.observe(viewLifecycleOwner) {
            if (it != null)
                thumbnailView.setImageURI(it)
        }
        model.updateChanged.observe(viewLifecycleOwner) {
            saveBtn.isEnabled = it
        }
        deleteBtn.setOnClickListener {
            showDeleteDialog()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populate() {
        val board = model.board.value ?: return
        isOwner = board.owner.id == Gson().fromJson(ZelinnApp.prefs.pull<String>(requireContext().getString(R.string.preference_current_user)), UserModel::class.java).id

        nameText.isEnabled = isOwner
        permissionBtn.isEnabled = isOwner
        saveBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
        deleteBtn.visibility = if (isOwner) View.VISIBLE else View.GONE
    }

    private fun createPermissionFragment() {
        val activity = requireActivity()
        val fragment = BoardPermissionFragment()

        activity.onBackPressedDispatcher.addCallback(backPressedCallback)
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, fragment, "BOARD_MENU_CONFIG_PERMISSION")
            .addToBackStack("BOARD_MENU_CONFIG_PERMISSION")
            .commit()
    }

    private fun closePermissionFragment() {
        backPressedCallback.remove()
        requireActivity().supportFragmentManager.popBackStack("BOARD_MENU_CONFIG_PERMISSION", FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun disable() {
        nameText.isEnabled = false
        permissionBtn.isEnabled = false
        saveBtn.isEnabled = false
        saveBtn.text = ""
        deleteBtn.isEnabled = false
        progressView.visibility = View.VISIBLE
    }

    private fun enable() {
        nameText.isEnabled = true
        permissionBtn.isEnabled = true
        saveBtn.isEnabled = true
        saveBtn.text = getString(R.string.common_save)
        deleteBtn.isEnabled = true
        progressView.visibility = View.GONE
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)

        titleView?.text = getString(R.string.board_menu_update_success)
        enable()

        dialog.setOnDismissListener {
            requireActivity().onBackPressed()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!
        enable()

        title.text = getString(R.string.board_menu_update_failed)
    }

    private fun uploadThumbnail() {
        val uri = model.updateUri.value!!
        val stream = requireActivity().contentResolver.openInputStream(uri) ?: return
        val cursor =
            requireActivity().contentResolver.query(uri, null, null, null, null) ?: return
        val filenameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val filename = cursor.getString(filenameIndex)
        val size = cursor.getLong(sizeIndex)
        cursor.close()
        val mimetype = requireActivity().contentResolver.getType(uri) ?: return
        val request = RequestBody.create(MediaType.parse(mimetype), stream.readBytes())
        val filePart = MultipartBody.Part.createFormData(
            "thumbnail",
            filename,
            request
        )

        RetrofitInstance.retrofit.uploadBoardThumbnail(filePart).enqueue(object :
            Callback<UploadBoardThumbnailResponse> {
            override fun onResponse(call: Call<UploadBoardThumbnailResponse>, response: Response<UploadBoardThumbnailResponse>) {
                val obj = response.body()

                if (response.isSuccessful && obj != null) {
                    postUpdateBoard(obj.url)
                } else showErrorDialog()
            }

            override fun onFailure(call: Call<UploadBoardThumbnailResponse>, t: Throwable) {
                showErrorDialog()
            }
        })
    }

    private fun postUpdateBoard(thumbnail: String) {
        val updateBoard = model.updateBoard.value!!
        val body = PostUpdateBoardBody(model.board.value!!.id, updateBoard.name, thumbnail, updateBoard.permission, updateBoard.owner.id, updateBoard.description)

        RetrofitInstance.retrofit.postUpdateBoard(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showSuccessDialog()
                    model.resetBoard()
                    ZelinnApp.prefs.push(getString(R.string.preference_boards_flag), true)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog()
            }
        })
    }

    private fun onUpdate() {
        disable()

        if (model.updateUri.value == null) postUpdateBoard(model.board.value!!.thumbnail)
        else uploadThumbnail()
    }

    private fun showDeleteDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_confirmation)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val descriptionView = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val thumbnailView = dialog.findViewById<CardView>(R.id.dialog_confirmation_thumbnail_card)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!

        descriptionView.visibility = View.GONE
        thumbnailView.visibility = View.GONE

        titleView.text = getString(R.string.board_menu_delete_confirmation)

        cancelBtn.setOnClickListener { dialog.dismiss() }
        confirmBtn.setOnClickListener { deleteBoard(); dialog.dismiss() }
    }

    private fun deleteBoard() {
        val boardId = (model.board.value?: return).id

        RetrofitInstance.retrofit.deleteBoard(boardId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_boards_flag), true)

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    requireActivity().startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }
}
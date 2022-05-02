package com.example.zelinn.ui.board

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentBoardConfigBinding
import com.example.zelinn.interfaces.PostUpdateBoardBody
import com.example.zelinn.interfaces.UploadBoardThumbnailResponse
import com.example.zelinn.ui.board_list.CreateBoardPermissionFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    private lateinit var archiveBtn: Button
    private lateinit var progressView: ProgressBar

    private var _binding: FragmentBoardConfigBinding? = null

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
        archiveBtn = root.findViewById(R.id.board_menu_config_archive_btn)
        progressView = root.findViewById(R.id.board_menu_config_progress)

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        nameText.doOnTextChanged { text, start, before, count ->
            model.setUpdateName(text.toString())
        }
        permissionBtn.setOnClickListener { createPermissionFragment() }
        thumbnailView.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imageLauncher.launch(photoPickerIntent)
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createPermissionFragment() {
        val activity = requireActivity()
        val fragment = CreateBoardPermissionFragment()

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
        archiveBtn.isEnabled = false
        progressView.visibility = View.VISIBLE
    }

    private fun enable() {
        nameText.isEnabled = true
        permissionBtn.isEnabled = true
        saveBtn.isEnabled = true
        saveBtn.text = getString(R.string.common_save)
        archiveBtn.isEnabled = true
        progressView.visibility = View.GONE
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)

        titleView?.text = getString(R.string.create_board_success)
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

        title.text = getString(R.string.profile_edit_failed)
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
        val body = PostUpdateBoardBody(model.board.value!!.id, updateBoard.name, thumbnail, updateBoard.permission)

        RetrofitInstance.retrofit.postUpdateBoard(body).enqueue(object: Callback<BoardModel> {
            override fun onResponse(call: Call<BoardModel>, response: Response<BoardModel>) {
                val board = response.body()

                if (response.isSuccessful && board != null) {
                    showSuccessDialog()
                    model.setBoard(board)
                }
            }

            override fun onFailure(call: Call<BoardModel>, t: Throwable) {
                showErrorDialog()
            }
        })
    }

    private fun onUpdate() {
        disable()

        if (model.updateUri.value == null) postUpdateBoard(model.board.value!!.thumbnail)
        else uploadThumbnail()
    }
}
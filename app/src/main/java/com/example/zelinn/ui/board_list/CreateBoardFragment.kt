package com.example.zelinn.ui.board_list

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentCreateBoardBinding
import com.example.zelinn.interfaces.PostBoardBody
import com.example.zelinn.interfaces.UploadBoardThumbnailResponse
import com.example.zelinn.ui.home.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateBoardFragment : Fragment() {
    private lateinit var createBtn: Button
    private lateinit var boardNameInput: TextInputEditText
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var thumbnailView: ImageView
    private lateinit var thumbnailButton: Button
    private lateinit var permissionBtn: Button
    private lateinit var progressView: ProgressBar

    private var _binding: FragmentCreateBoardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val model: HomeViewModel by activityViewModels()
    private val imageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                model.setBoardThumbnail(it.data!!.data!!)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBoardBinding.inflate(inflater, container, false)
        val root = binding.root
        val closeBtn = root.findViewById<Button>(R.id.create_board_close_btn)
        permissionBtn = root.findViewById(R.id.create_board_permission_btn)
        createBtn = root.findViewById(R.id.create_board_create_btn)
        boardNameInput = root.findViewById(R.id.create_board_name_input)
        thumbnailButton = root.findViewById(R.id.create_board_thumbnail_btn)
        thumbnailView = root.findViewById(R.id.create_board_thumbnail)
        progressView = root.findViewById(R.id.create_board_progress)
        backPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backPressedCallback.remove()
                requireActivity().supportFragmentManager.popBackStack("CREATE_BOARD_PERMISSION", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        createBtn.setOnClickListener {
            uploadThumbnail()
        }
        boardNameInput.doOnTextChanged { text, start, before, count ->
            model.setBoardName(text.toString())
            checkValid()
        }
        thumbnailButton.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imageLauncher.launch(photoPickerIntent)
        }
        thumbnailView.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imageLauncher.launch(photoPickerIntent)
        }

        permissionBtn.setOnClickListener {
            val permissionFragment = CreateBoardPermissionFragment()
            val activity = requireActivity()

            activity.onBackPressedDispatcher.addCallback(backPressedCallback)
            activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom,
                    R.anim.slide_in_bottom,
                    R.anim.slide_out_bottom
                )
                .replace(R.id.board_list_create_board_fragment, permissionFragment, "CREATE_BOARD_PERMISSION")
                .addToBackStack("CREATE_BOARD_PERMISSION")
                .commit()
        }

        boardNameInput.setText(model.boardName.value)
        model.boardPermission.observe(viewLifecycleOwner) {
            if (it == getString(R.string.board_public_value))
                permissionBtn.setText(getString(R.string.permission_public))
            else permissionBtn.setText(getString(R.string.permission_private))
        }
        model.boardThumbnail.observe(viewLifecycleOwner) {
            if (it != null) {
                thumbnailView.setImageURI(it)
                thumbnailView.visibility = View.VISIBLE
                checkValid()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkValid() {
        val boardName = model.boardName.value
        val boardThumbnail = model.boardThumbnail.value

        createBtn.isEnabled = !boardName.isNullOrEmpty() && boardThumbnail != null
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)

        titleView?.text = getString(R.string.create_board_success)
        onFinishPost()

        dialog.setOnDismissListener {
            findNavController().popBackStack()
        }
    }

    private fun showErrorDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!
        onFinishPost()

        title.text = getString(R.string.profile_edit_failed)
    }

    private fun onPosting() {
        permissionBtn.isEnabled = false
        createBtn.isEnabled = false
        createBtn.text = ""
        progressView.visibility = View.VISIBLE
    }

    private fun onFinishPost() {
        permissionBtn.isEnabled = true
        createBtn.isEnabled = true
        createBtn.text = getString(R.string.create_action)
        progressView.visibility = View.GONE
    }

    private fun uploadThumbnail() {
        val uri = model.boardThumbnail.value!!
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

        onPosting()

        RetrofitInstance.retrofit.uploadBoardThumbnail(filePart).enqueue(object : Callback<UploadBoardThumbnailResponse> {
            override fun onResponse(call: Call<UploadBoardThumbnailResponse>, response: Response<UploadBoardThumbnailResponse>) {
                val obj = response.body()

                if (response.isSuccessful && obj != null) {
                    postBoard(obj.url)
                } else showErrorDialog()
            }

            override fun onFailure(call: Call<UploadBoardThumbnailResponse>, t: Throwable) {
                showErrorDialog()
            }
        })
    }

    private fun postBoard(thumbnail: String) {
        val body = PostBoardBody(model.boardName.value!!, thumbnail, model.boardPermission.value!!)

        RetrofitInstance.retrofit.postBoard(body).enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    model.setBoardsFlag(true)
                    showSuccessDialog()
                } else showErrorDialog()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showErrorDialog()
            }
        })
    }
}
package com.example.zelinn.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.zelinn.R
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentProfileEditBinding
import com.example.zelinn.interfaces.PostEditUserBody
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import io.ak1.BubbleTabBar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileEditFragment : Fragment() {
    private var _binding: FragmentProfileEditBinding? = null
    private lateinit var avatarView: ImageView
    private lateinit var nameView: EditText
    private lateinit var emailView: EditText
    private lateinit var phoneView: EditText
    private lateinit var saveBtn: Button
    private lateinit var uploadBtn: ConstraintLayout
    private lateinit var backBtn: Button
    private lateinit var progressView: ProgressBar
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        val root: View = binding.root

        avatarView = root.findViewById(R.id.profile_edit_avatar)
        nameView = root.findViewById(R.id.profile_edit_name)
        emailView = root.findViewById(R.id.profile_edit_email)
        phoneView = root.findViewById(R.id.profile_edit_phone)
        saveBtn = root.findViewById(R.id.profile_edit_action_btn)
        uploadBtn = root.findViewById(R.id.profile_edit_avatar_layout)
        backBtn = root.findViewById(R.id.profile_edit_back_btn)
        progressView = root.findViewById(R.id.profile_edit_progress)

        imageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    imageUri = it.data!!.data!!
                    avatarView.setImageURI(imageUri)
                }
            }

        populate()
        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_profile, false)

        saveBtn.setOnClickListener {
            onSave()
        }
        backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        uploadBtn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imageLauncher.launch(photoPickerIntent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun populate() {
        val user = Hawk.get<UserModel>(getString(R.string.preference_current_user))

        if (!user.avatar.isNullOrEmpty())
            Glide
                .with(this)
                .load(user.avatar)
                .into(avatarView)

        nameView.setText(user.name)
        emailView.setText(user.email)
        phoneView.setText(user.phone)
    }

    private fun showErrorDialog(code: Int?) {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_error)
        val dialog = builder.show()

        val title = dialog.findViewById<TextView>(R.id.dialog_error_title)!!

        onFinishEdit()

        if (code == null) title.text = getString(R.string.profile_edit_failed)
        else {
            when(code) {
                409 -> title.text = "Số điện thoại này đã có người dùng khác sử dụng!"
                else -> title.text = getString(R.string.profile_edit_failed)
            }
        }
    }

    private fun showSuccessDialog() {
        val builder = MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_success)
        val dialog = builder.show()

        val titleView = dialog.findViewById<TextView>(R.id.dialog_success_title)

        titleView?.text = getString(R.string.profile_edit_success)
        onFinishEdit()

        dialog.setOnDismissListener {
            findNavController().popBackStack()
        }
    }

    private fun onEditing() {
        uploadBtn.isEnabled = false
        nameView.isEnabled = false
        phoneView.isEnabled = false
        saveBtn.isEnabled = false
        saveBtn.text = ""
        progressView.visibility = View.VISIBLE
    }

    private fun onFinishEdit() {
        uploadBtn.isEnabled = true
        nameView.isEnabled = true
        phoneView.isEnabled = true
        saveBtn.isEnabled = true
        saveBtn.text = getString(R.string.common_save)
        progressView.visibility = View.GONE
    }

    @SuppressLint("Range")
    private fun postUploadAvatar() {
        val stream = requireActivity().contentResolver.openInputStream(imageUri!!) ?: return
        val cursor =
            requireActivity().contentResolver.query(imageUri!!, null, null, null, null) ?: return
        val filenameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val filename = cursor.getString(filenameIndex)
        val size = cursor.getLong(sizeIndex)
        cursor.close()
        val mimetype = requireActivity().contentResolver.getType(imageUri!!) ?: return
        val request = RequestBody.create(MediaType.parse(mimetype), stream.readBytes())
        val filePart = MultipartBody.Part.createFormData(
            "avatar",
            filename,
            request
        )

        RetrofitInstance.retrofit.uploadAvatarUser(filePart).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                val user = response.body()

                if (response.isSuccessful && user != null) {
                    Hawk.put(getString(R.string.preference_current_user), user)
                    showSuccessDialog()
                } else showErrorDialog(response.code())
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                showErrorDialog(null)
            }
        })
    }

    private fun onSave() {
        val name = nameView.text.toString()
        val phone = phoneView.text.toString()

        val body = PostEditUserBody(name, phone)

        onEditing()

        RetrofitInstance.retrofit.editUser(body).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                val user = response.body()

                if (response.isSuccessful && user != null) {
                    if (imageUri != null)
                        postUploadAvatar()
                    else {
                        Hawk.put(getString(R.string.preference_current_user), user)
                        showSuccessDialog()
                    }
                } else showErrorDialog(response.code())
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                showErrorDialog(null)
            }
        })
    }
}
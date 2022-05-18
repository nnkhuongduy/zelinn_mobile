package com.example.zelinn.ui.board

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.zelinn.HomeActivity
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardMenuBinding
import com.example.zelinn.interfaces.LeaveBoardBody
import com.example.zelinn.interfaces.PostUserFavBoardBody
import com.example.zelinn.ui.board.info.BoardMenuInfoFragment
import com.example.zelinn.ui.board.invite.BoardMenuInviteFragment
import com.example.zelinn.ui.board.member_list.BoardMemberListFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardMenuFragment : Fragment() {
    private var _binding: FragmentBoardMenuBinding? = null
    private lateinit var currentTag: String
    private lateinit var starBtnUnfav: Button
    private lateinit var starBtnFav: Button
    private lateinit var currentStarBtn: Button
    private lateinit var shareBtn: Button
    private lateinit var inviteBtn: Button
    private lateinit var memberRV: RecyclerView
    private lateinit var memberBtn: Button
    private lateinit var infoCard: CardView
    private lateinit var configBtn: Button
    private lateinit var closeBtn: Button
    private lateinit var configLayout: LinearLayout
    private lateinit var leaveLayout: LinearLayout
    private lateinit var leaveBtn: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            closeFragment()
        }
    }
    private val model: BoardViewModel by activityViewModels()
    private val adapter = BoardMenuMemberAdapter()

    private var isOwner = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardMenuBinding.inflate(inflater, container, false)
        val root = binding.root

        findViews()
        populate()
        watch()


        updateFavorite()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        closeBtn = binding.root.findViewById(R.id.board_menu_close)
        configBtn = binding.root.findViewById(R.id.board_menu_config_btn)
        starBtnUnfav = binding.root.findViewById(R.id.board_menu_star_btn_unfav)
        starBtnFav = binding.root.findViewById(R.id.board_menu_star_btn_fav)
        shareBtn = binding.root.findViewById(R.id.board_menu_share_btn)
        inviteBtn = binding.root.findViewById(R.id.board_menu_invite_btn)
        memberRV = binding.root.findViewById(R.id.board_menu_member_rv)
        memberBtn = binding.root.findViewById(R.id.board_menu_members_btn)
        infoCard = binding.root.findViewById(R.id.board_menu_info_btn)
        configLayout = binding.root.findViewById(R.id.board_menu_config_layout)
        leaveLayout = binding.root.findViewById(R.id.board_menu_leave_layout)
        leaveBtn = binding.root.findViewById(R.id.board_menu_leave_btn)
    }

    private fun populate() {
        memberRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        memberRV.adapter = adapter

        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
        val board = model.board.value!!
        isOwner = board.owner.id == user.id

        updateFavorite()

        configLayout.visibility = if (isOwner) View.VISIBLE else View.GONE
        leaveLayout.visibility = if (isOwner) View.GONE else View.VISIBLE
    }

    private fun watch() {
        closeBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }
        configBtn.setOnClickListener {
            model.resetUpdateBoard()
            val fragment = BoardMenuConfigFragment()
            createFragment(fragment, "BOARD_MENU_CONFIG")
        }
        starBtnUnfav.setOnClickListener { postFavBoard() }
        starBtnFav.setOnClickListener { postFavBoard() }
        inviteBtn.setOnClickListener {
            val fragment = BoardMenuInviteFragment()
            createFragment(fragment, "BOARD_MENU_INVITE")
        }
        memberBtn.setOnClickListener {
            val fragment = BoardMemberListFragment()
            createFragment(fragment, "BOARD_MEMBER_LIST")
        }
        infoCard.setOnClickListener {
            model.resetUpdateBoard()
            val fragment = BoardMenuInfoFragment()
            createFragment(fragment, "BOARD_MENU_INFO")
        }
        leaveBtn.setOnClickListener {
            toggleConfirmLeave()
        }
        model.board.observe(viewLifecycleOwner) {
            val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
            val isPublic = it.permission == getString(R.string.board_public_value)
            val isOwner = it.owner.id == user.id
            shareBtn.isEnabled = isPublic
            shareBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isPublic) R.color.permission_public else R.color.background_surface))
            inviteBtn.isEnabled = isOwner
            inviteBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isOwner) R.color.permission_private else R.color.background_surface))
            adapter.apply {
                submitList(it.members)
            }
        }
    }

    private fun createFragment(fragment: Fragment, tag: String) {
        val activity = requireActivity()

        currentTag = tag

        activity.onBackPressedDispatcher.addCallback(backPressedCallback)
        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom,
                R.anim.slide_in_bottom,
                R.anim.slide_out_bottom
            )
            .replace(R.id.board_activity_fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun closeFragment() {
        val activity = requireActivity()

        backPressedCallback.remove()
        activity.supportFragmentManager.popBackStack(
            currentTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    private fun updateFavorite() {
        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
        val board = model.board.value!!

        if (user.favBoards.contains(board.id)) {
            starBtnFav.visibility = View.VISIBLE
            starBtnUnfav.visibility = View.GONE
            currentStarBtn = starBtnFav
        } else {
            starBtnFav.visibility = View.GONE
            starBtnUnfav.visibility = View.VISIBLE
            currentStarBtn = starBtnUnfav
        }
    }

    private fun toggleFavButtonState() {
        if (currentStarBtn.isEnabled) {
            currentStarBtn.isEnabled = false
            currentStarBtn.backgroundTintList = resources.getColorStateList(R.color.gray3)
        } else {
            currentStarBtn.isEnabled = true
            currentStarBtn.backgroundTintList = resources.getColorStateList(R.color.gold)
        }
    }

    private fun toggleLeaveBtn() {
        if (leaveBtn.isEnabled) {
            leaveBtn.isEnabled = false
            leaveBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.gray3)
        } else {
            leaveBtn.isEnabled = true
            leaveBtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.danger)
        }
    }

    private fun toggleConfirmLeave() {
        val builder =
            MaterialAlertDialogBuilder(requireContext()).setView(R.layout.dialog_confirmation)
        val dialog = builder.show()

        val board = model.board.value!!

        val thumbnailView = dialog.findViewById<ImageView>(R.id.dialog_confirmation_thumbnail)!!
        val titleView = dialog.findViewById<TextView>(R.id.dialog_confirmation_title)!!
        val subtitleView = dialog.findViewById<TextView>(R.id.dialog_confirmation_description)!!
        val confirmBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_confirm_btn)!!
        val cancelBtn = dialog.findViewById<Button>(R.id.dialog_confirmation_cancel_btn)!!

        titleView.text = board.name
        subtitleView.text = getString(R.string.board_menu_leave_confirmation)
        Glide.with(this).load(board.thumbnail).into(thumbnailView)

        confirmBtn.setOnClickListener {
            leaveBoard()
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun postFavBoard() {
        toggleFavButtonState()
        val body = PostUserFavBoardBody(model.board.value!!.id)

        RetrofitInstance.retrofit.userFavBoard(body).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                toggleFavButtonState()
                val user = response.body()

                if (response.isSuccessful && user != null) {
                    ZelinnApp.prefs.push(getString(R.string.preference_current_user), Gson().toJson(user))
                    ZelinnApp.prefs.push(getString(R.string.preference_user_flag), true)
                    updateFavorite()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                toggleFavButtonState()
            }
        })
    }

    private fun leaveBoard() {
        val body = LeaveBoardBody(model.board.value!!.id)
        toggleLeaveBtn()

        RetrofitInstance.retrofit.leaveBoard(body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                toggleLeaveBtn()

                if (response.isSuccessful) {
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), true)

                    val intent = Intent(requireContext(), HomeActivity::class.java)
                    requireActivity().startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                toggleLeaveBtn()
            }
        })
    }
}
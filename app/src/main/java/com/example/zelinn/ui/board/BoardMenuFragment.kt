package com.example.zelinn.ui.board

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zelinn.R
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentBoardMenuBinding
import com.example.zelinn.interfaces.PostUserFavBoardBody
import com.example.zelinn.ui.board.info.BoardMenuInfoFragment
import com.example.zelinn.ui.board.invite.BoardMenuInviteFragment
import com.example.zelinn.ui.board.member_list.BoardMemberListFragment
import com.orhanobut.hawk.Hawk
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardMenuBinding.inflate(inflater, container, false)
        val root = binding.root

        val closeBtn = root.findViewById<Button>(R.id.board_menu_close)
        val configBtn = root.findViewById<Button>(R.id.board_menu_config_btn)
        starBtnUnfav = root.findViewById(R.id.board_menu_star_btn_unfav)
        starBtnFav = root.findViewById(R.id.board_menu_star_btn_fav)
        shareBtn = root.findViewById(R.id.board_menu_share_btn)
        inviteBtn = root.findViewById(R.id.board_menu_invite_btn)
        memberRV = root.findViewById(R.id.board_menu_member_rv)
        memberBtn = root.findViewById(R.id.board_menu_members_btn)
        infoCard = root.findViewById(R.id.board_menu_info_btn)

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

        memberRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        memberRV.adapter = adapter

        model.board.observe(viewLifecycleOwner) {
            val isPublic = it.permission == getString(R.string.board_public_value)
            val isOwner = it.owner.id == Hawk.get<UserModel>(getString(R.string.preference_current_user)).id
            shareBtn.isEnabled = isPublic
            shareBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isPublic) R.color.permission_public else R.color.background_surface))
            inviteBtn.isEnabled = isOwner
            inviteBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), if (isOwner) R.color.permission_private else R.color.background_surface))
            adapter.apply {
                submitList(it.members)
            }
        }
        updateFavorite()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val user = Hawk.get<UserModel>(getString(R.string.preference_current_user))
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


    private fun postFavBoard() {
        toggleFavButtonState()
        val body = PostUserFavBoardBody(model.board.value!!.id)

        RetrofitInstance.retrofit.userFavBoard(body).enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                toggleFavButtonState()
                val user = response.body()

                if (response.isSuccessful && user != null) {
                    Hawk.put(getString(R.string.preference_current_user), user)
                    Hawk.put(getString(R.string.preference_user_flag), true)
                    updateFavorite()
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                toggleFavButtonState()
            }
        })
    }
}
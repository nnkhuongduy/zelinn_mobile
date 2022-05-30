package com.example.zelinn.ui.pepper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.viewpager2.widget.ViewPager2
import com.cesarferreira.tempo.Tempo
import com.cesarferreira.tempo.days
import com.cesarferreira.tempo.minus
import com.cesarferreira.tempo.plus
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.OnSwipeTouchListener
import com.example.zelinn.classes.UserModel
import com.example.zelinn.databinding.FragmentPepperBinding
import com.google.gson.Gson
import io.ak1.BubbleTabBar

class PepperFragment: Fragment() {
    private lateinit var titleText: TextView
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var viewPager: ViewPager2

    private var _binding: FragmentPepperBinding? = null
    private var date = Tempo.now

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPepperBinding.inflate(inflater, container, false)
        val root: View = binding.root

        requireActivity().findViewById<BubbleTabBar>(R.id.bottom_nav_menu).setSelectedWithId(R.id.navigation_pepper, false)

        findViews()
        init()

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        this.titleText = binding.root.findViewById(R.id.pepper_greeting)
        this.fragmentContainer = binding.root.findViewById(R.id.pepper_fragment_container)
        this.viewPager = binding.root.findViewById(R.id.pepper_viewpager)
    }

    private fun init() {
        val user = Gson().fromJson(ZelinnApp.prefs.pull<String>(getString(R.string.preference_current_user)), UserModel::class.java)
        val activity = requireActivity()

        this.titleText.text = "${getString(R.string.pepper_greeting)} ${if (user.name.length > 10) user.name.substring(0, 10) + "..." else user.name} \uD83D\uDC4B"


        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.pepper_fragment_container, PepperItemFragment(date))
            .commit()
    }

    fun toPrevDay() {
        val activity = requireActivity()
        this.date = this.date - 1.days

        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
            )
            .replace(R.id.pepper_fragment_container, PepperItemFragment(this.date))
            .commit()
    }

    fun toNextDay() {
        val activity = requireActivity()
        this.date = this.date + 1.days

        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_right,
                R.anim.exit_to_left,
            )
            .replace(R.id.pepper_fragment_container, PepperItemFragment(this.date))
            .commit()
    }
}
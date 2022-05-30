package com.example.zelinn.ui.pepper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cesarferreira.tempo.isToday
import com.cesarferreira.tempo.isTomorrow
import com.cesarferreira.tempo.isYesterday
import com.cesarferreira.tempo.toString
import com.example.zelinn.R
import com.example.zelinn.ZelinnApp
import com.example.zelinn.classes.OnSwipeTouchListener
import com.example.zelinn.classes.PepperModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.FragmentPepperItemBinding
import com.example.zelinn.ui.pepper.board.PepperBoardAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class PepperItemFragment(val date: Date): Fragment() {
    private lateinit var dateText: TextView
    private lateinit var listView: RecyclerView
    private lateinit var prevBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var swipeView: SwipeRefreshLayout

    private var _binding: FragmentPepperItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val adapter = PepperBoardAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPepperItemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        ZelinnApp.prefs.push(getString(R.string.preference_pepper_flag), false)

        findViews()
        init()
        watch()

        return root;
    }

    override fun onResume() {
        super.onResume()

        try {
            val flag = ZelinnApp.prefs.pull<Boolean>(getString(R.string.preference_pepper_flag))

            if (flag != null && flag)
                getPepper()
        } catch (e: Exception) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun findViews() {
        this.dateText = binding.root.findViewById(R.id.pepper_item_date)
        this.listView = binding.root.findViewById(R.id.pepper_item_list)
        this.prevBtn = binding.root.findViewById(R.id.pepper_item_prev_btn)
        this.nextBtn = binding.root.findViewById(R.id.pepper_item_next_btn)
        this.swipeView = binding.root.findViewById(R.id.pepper_item_swipe)
    }

    private fun init() {
        var dateText = SimpleDateFormat("'Ngày' d 'tháng' M").format(this.date)

        if (this.date.isToday) dateText = getString(R.string.pepper_today)
        if (this.date.isYesterday) dateText = getString(R.string.pepper_yesterday)
        if (this.date.isTomorrow) dateText = getString(R.string.pepper_tomorrow)

        this.dateText.text = dateText

        getPepper()

        this.listView.layoutManager = LinearLayoutManager(requireContext())
        this.listView.adapter = adapter
    }

    private fun watch () {
        this.prevBtn.setOnClickListener {
            requireActivity().supportFragmentManager.findFragmentById(R.id.home_activity_view)?.let {
                it.childFragmentManager.primaryNavigationFragment?.let {
                    (it as PepperFragment).toPrevDay()
                }
            }
        }
        this.nextBtn.setOnClickListener {
            requireActivity().supportFragmentManager.findFragmentById(R.id.home_activity_view)?.let {
                it.childFragmentManager.primaryNavigationFragment?.let {
                    (it as PepperFragment).toNextDay()
                }
            }
        }
        this.swipeView.setOnRefreshListener {
            getPepper()
        }
    }

    private fun getPepper() {
        RetrofitInstance.retrofit.getPepper(this.date.toString("yyyy-MM-dd'T'HH:mm'Z'")).enqueue(object: Callback<List<PepperModel>> {
            override fun onResponse(call: Call<List<PepperModel>>, response: Response<List<PepperModel>>) {
                this@PepperItemFragment.swipeView.isRefreshing = false
                ZelinnApp.prefs.push(getString(R.string.preference_pepper_flag), false)

                val pepper = response.body()

                if (response.isSuccessful && pepper != null) {
                    if (pepper.isNotEmpty())
                        pepper[0].expanded = true

                    adapter.apply {
                        submitList(pepper)
                    }
                }
            }

            override fun onFailure(call: Call<List<PepperModel>>, t: Throwable) {
                Log.d("failed", "failed")
            }
        })
    }
}

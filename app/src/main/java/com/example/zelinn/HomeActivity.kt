package com.example.zelinn

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.ActivityHomeBinding
import com.example.zelinn.ui.home.HomeBoardListFragment
import com.example.zelinn.ui.home.HomeFragment
import com.example.zelinn.ui.home.HomeViewModel
import com.example.zelinn.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val model: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.bottomNavMenu

        navController = (supportFragmentManager.findFragmentById(R.id.home_activity_view) as NavHostFragment).navController
        model.boardsFlag.observe(this) {
            if (it) getBoards(null)
        }

        navView.addBubbleListener() {id ->
            when (id) {
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_home)
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                }
                R.id.navigation_notification -> {
                    navController.navigate(R.id.navigation_notification)
                }
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()

//        if (ZinnApp.prefs.pull(getString(R.string.preference_board_flag), false)) {
//            getelBoards(null)
//        }
    }

    override fun onBackPressed() {
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
        if (navController.popBackStack().not()) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        try {
            if (ZelinnApp.prefs.pull(getString(R.string.preference_board_flag), false)) {
                getBoards(null)
            }
        } catch (e: Exception) {

        }
    }

    fun getBoards(layout: SwipeRefreshLayout?) {
        RetrofitInstance.retrofit.getBoards().enqueue(object: Callback<List<BoardModel>> {
            override fun onResponse(
                call: Call<List<BoardModel>>,
                response: Response<List<BoardModel>>
            ) {
                val boards = response.body()
                if (response.isSuccessful && boards != null) {
                    model.setBoards(boards)
                    model.setBoardsFlag(false)
                    ZelinnApp.prefs.push(getString(R.string.preference_board_flag), false)

                    if (layout != null) layout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {}
        })
    }
}
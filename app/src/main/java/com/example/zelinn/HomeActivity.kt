package com.example.zelinn

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.ActivityHomeBinding
import com.example.zelinn.ui.boards.BoardsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView = binding.bottomNavMenu

        navController = (supportFragmentManager.findFragmentById(R.id.home_activity_view) as NavHostFragment).navController

        navView.addBubbleListener() {id ->
            when (id) {
                R.id.menu_pepper -> {
                    navController.navigate(R.id.navigation_pepper)
                }
                R.id.menu_boards -> {
                    navController.navigate(R.id.navigation_boards)
                }
                R.id.menu_profile -> {
                    navController.navigate(R.id.navigation_profile)
                }
                R.id.menu_notification -> {
                    navController.navigate(R.id.navigation_notification)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
            return
        }
        if (navController.popBackStack().not()) {
            finish()
            return
        }
    }
}
package com.example.zelinn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.zelinn.databinding.ActivityHomeBinding
import com.example.zelinn.ui.home.HomeBoardListFragment
import com.example.zelinn.ui.home.HomeFragment
import com.example.zelinn.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: NavigationBarView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home)
        navView.setupWithNavController(navController)
        navView.setOnItemSelectedListener(menuNavigationItemSelectedListener)

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        val boardList = HomeBoardListFragment()

        transaction.replace(R.id.homepage_content_fragment, boardList)
        transaction.commit()
    }

    override fun onBackPressed() {
        if (onBackPressedDispatcher.hasEnabledCallbacks()) {
            onBackPressedDispatcher.onBackPressed()
        } else if (navController.popBackStack().not()) {
            finish()
        }
    }

    fun setBottomNavVisibility(visibility: Int) {
        binding.navView.visibility = visibility
    }

    private val menuNavigationItemSelectedListener =
        NavigationBarView.OnItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    navController.navigate(R.id.navigation_home)
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                }
            }
            false
        }
}
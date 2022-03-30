package com.example.zelinn

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.createViewModelLazy
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.databinding.ActivityHomeBinding
import com.example.zelinn.ui.home.HomeBoardItemFragment
import com.example.zelinn.ui.home.HomeBoardListFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var tab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        val toolbar = findViewById<Toolbar>(R.id.homepage_toolbar)
        setSupportActionBar(toolbar)
        navView.setupWithNavController(navController)

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        val boardList = HomeBoardListFragment();

        transaction.replace(R.id.homepage_content_fragment, boardList)

        RetrofitInstance.retrofit.getBoards().enqueue(object: Callback<List<BoardModel>> {
            override fun onFailure(call: Call<List<BoardModel>>, t: Throwable) {
                Log.d("Test", "test")
            }
            override fun onResponse(
                call: Call<List<BoardModel>>,
                response: Response<List<BoardModel>>
            ) {
                val boards = response.body()

                Log.d("Test", boards.toString())

                boards?.let {
                    for (board in boards) {
                        val item = HomeBoardItemFragment.newInstance(board)

                        supportFragmentManager.beginTransaction().add(R.id.homepage_board_list_item_layout, item, board.id).commit()
                    }
                }
            }
        })
    }

}
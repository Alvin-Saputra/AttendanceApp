package com.example.attendanceapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
//import androidx.glance.visibility
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.ActivityMainBinding
import com.example.attendanceapp.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val navigateTo = intent.getStringExtra("navigateTo")

        if (navigateTo == "Account") {
            binding.navView.selectedItemId = R.id.navigation_account

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.navigation_home, true)
                .build()

            navController.navigate(R.id.navigation_account, null, navOptions)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_attendance_history, R.id.navigation_account
                )
            )
//        setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)


            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.mapsFragment || destination.id == R.id.selfieFragment || destination.id == R.id.successFragment) {
                    // Sembunyikan BottomNavigationView
                    navView.visibility = View.GONE
                } else {
                    // Tampilkan BottomNavigationView
                    navView.visibility = View.VISIBLE
                }
            }


        }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
    }

package com.example.attendanceapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
//import androidx.glance.visibility
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.ActivityMainBinding
import com.example.attendanceapp.ui.main.ViewModelFactory

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

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {

                navController.navigate(R.id.action_navigation_home_to_loginFragment)
            }
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
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
}
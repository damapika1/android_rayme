package com.hogent.squads.view

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hogent.squads.R
import com.hogent.squads.databinding.ActivityMainBinding
import com.hogent.squads.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Main activity created")
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_main_activity) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = binding.bottomNavigation
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        checkToDisplayLoginFragment()
    }

    private fun checkToDisplayLoginFragment() {
        userViewModel.activeUser.observe(this) { userData ->
            if (userData == null) {
                clearBottomNav()
                navController.navigate(R.id.loginFragment)
            } else {
                setupBottomNav()
            }
        }
    }

    private fun clearBottomNav() {
        bottomNavigation.visibility = View.INVISIBLE
    }

    private fun setupBottomNav() {
        bottomNavigation.visibility = View.VISIBLE
        bottomNavigation.menu.findItem(R.id.bottom_nav_calendar).isChecked = true
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_nav_user_home -> {
                    navController.navigate(MainActivityDirections.actionGlobalNavGraphHome())
                    true
                }

                R.id.bottom_nav_calendar -> {
                    navController.navigate(MainActivityDirections.actionGlobalNavGraphCalendar())
                    true
                }

                R.id.bottom_nav_stats -> {
                    navController.navigate(MainActivityDirections.actionGlobalNavGraphStats())
                    true
                }

                else -> false
            }
        }
    }
}

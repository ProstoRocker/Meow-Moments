package com.ilyadev.meowmoments.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ilyadev.meowmoments.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // <-- Добавь эту аннотацию
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Поддержка ActionBar не требуется для BottomNavigationView, но setupActionBarWithNavController может быть полезен
        // val appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.calendarFragment, /* ... */))
        // setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
package com.example.trailguide

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trailguide.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var settingsFragment: settingsfragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the landing page layout
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Apply insets to main content view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Display landing page for 3 seconds before navigating to HomeFragment
        Handler(Looper.getMainLooper()).postDelayed({
            setupHomeScreen()
        }, 3000)
    }

    private fun setupHomeScreen() {
        // Set main activity layout for home and setup bottom navigation
        setContentView(R.layout.activity_home)

        // Load default fragment (Home)
        loadFragment(homefragment())

        // Initialize bottom navigation and set the listener
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homepage -> loadFragment(homefragment())
                R.id.mappage -> loadFragment(MapFragment())
                R.id.notificationpage -> loadFragment(notificationfragment())
                R.id.preferncespage -> loadFragment(preferencesFragment())
                R.id.settingspage -> {
                    // Initialize settingsFragment if needed and load it
                    if (!::settingsFragment.isInitialized) settingsFragment = settingsfragment()
                    loadFragment(settingsFragment)
                }
            }
            true
        }
    }

    // Function to load selected fragment
    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}

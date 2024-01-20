package com.changs.android.gnuting_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.databinding.ActivityMainBinding
import com.changs.android.gnuting_android.util.setFullScreenWithStatusBar
import com.changs.android.gnuting_android.util.setStatusBarColor

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFullScreenWithStatusBar(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.startFragment -> setStatusBarColor(R.color.transparent)
                else -> setStatusBarColor(R.color.white)
            }
        }
    }
}
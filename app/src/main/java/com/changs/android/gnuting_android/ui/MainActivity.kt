package com.changs.android.gnuting_android.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.changs.android.gnuting_android.databinding.ActivityMainBinding
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val accessToken = runBlocking { viewModel.getAccessToken().firstOrNull() }
        Timber.d("Token: $accessToken")

        splashScreen.setOnExitAnimationListener {
            if (accessToken != null) {
                val intent = Intent(this@MainActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                it.remove()
            }
        }

        viewModel.spinner.observe(this) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        viewModel.toast.eventObserve(this) { text ->
            text?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

    }
}
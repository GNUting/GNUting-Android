package com.changs.android.gnuting_android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import com.changs.android.gnuting_android.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        setContentView(R.layout.activity_splash)

        splashScreen.setKeepOnScreenCondition { true }

        splashViewModel.isLoading.observe(this) {
            if (it == false) {
                val accessToken = runBlocking {
                    viewModel.getAccessToken().firstOrNull()
                }

                Timber.d("Token: $accessToken")

                splashScreen.setKeepOnScreenCondition { false }

                if (accessToken != null) {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

            }
        }

    }
}
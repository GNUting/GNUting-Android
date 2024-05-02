package com.changs.android.gnuting_android.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.databinding.ActivityHomeBinding
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var toast: Toast? = null
    private val viewModel: HomeMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val accessToken = runBlocking { viewModel.getAccessToken().firstOrNull() }

        if (accessToken == null) {
            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        initFirebaseFcm()

        askNotificationPermission()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.home_nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.listFragment, R.id.chatListFragment, R.id.myFragment -> binding.bottomNav.isVisible =
                    true

                else -> binding.bottomNav.isVisible = false
            }
        }

        viewModel.toast.eventObserve(this) { text ->
            text?.let {
                showToast(it)
            }
        }

        viewModel.spinner.observe(this) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        val location = intent.getStringExtra("location")

        location?.let {
            when (it) {
                "chat" -> selectedItemId(R.id.chatListFragment)
                else -> selectedItemId(R.id.listFragment)
            }
        }

    }

    private fun initFirebaseFcm() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            viewModel.postSaveFcmToken(it)
        }.addOnFailureListener {
            showToast("네트워크 에러가 발생했습니다.")
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {

        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun selectedItemId(menuId: Int) {
        binding.bottomNav.selectedItemId = menuId
    }

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        toast = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val location = intent?.getStringExtra("location")

        location?.let {
            when (it) {
                "chat" -> selectedItemId(R.id.chatListFragment)
                else -> selectedItemId(R.id.listFragment)
            }
        }
    }
}
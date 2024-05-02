package com.changs.android.gnuting_android.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.changs.android.gnuting_android.databinding.ActivityMainBinding
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
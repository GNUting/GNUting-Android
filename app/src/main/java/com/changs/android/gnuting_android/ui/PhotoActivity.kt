package com.changs.android.gnuting_android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.databinding.ActivityPhotoBinding
class PhotoActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPhotoBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val img = intent.getStringExtra("img")
        Glide.with(this@PhotoActivity).load(img).error(R.drawable.ic_profile).into(binding.photoImg)

        binding.photoImgClose.setOnClickListener {
            finish()
        }
    }
}
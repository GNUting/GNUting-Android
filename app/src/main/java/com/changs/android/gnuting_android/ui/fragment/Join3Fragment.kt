package com.changs.android.gnuting_android.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentJoin3Binding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.getBitmap
import com.changs.android.gnuting_android.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class Join3Fragment :
    BaseFragment<FragmentJoin3Binding>(FragmentJoin3Binding::bind, R.layout.fragment_join3) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.join3ImgBack.setOnClickListener { findNavController().popBackStack() }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Timber.d("Selected URI: $uri")
                    viewModel.profileImage = uri.getBitmap(requireContext().contentResolver)
                    Glide.with(this).load(uri).circleCrop().error(R.drawable.ic_profile)
                        .into(binding.join3ImgProfile)
                } else {
                    Timber.d("No media selected")
                }
            }

        binding.join3ImgProfile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.join3BtnNext.setOnClickListener {
            viewModel.postSignUp()
        }
    }

    private fun setObserver() {
        viewModel.signUpResponse.eventObserve(viewLifecycleOwner) {
            if (it.isSuccess == true) {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }
}
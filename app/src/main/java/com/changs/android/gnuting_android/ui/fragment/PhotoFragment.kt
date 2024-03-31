package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPhotoBinding
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class PhotoFragment : BaseFragment<FragmentPhotoBinding>(FragmentPhotoBinding::bind, R.layout.fragment_photo) {
    private val args: PhotoFragmentArgs by navArgs()
    private val viewModel: HomeMainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.user?.let { user ->
            Glide.with(requireContext()).load(user.profileImage).error(R.drawable.ic_profile).into(binding.photoImg)

            if ((viewModel.myInfo.value?.nickname) == user.nickname) {
                binding.photoImgReport.isVisible = false
            } else {
                binding.photoImgReport.setOnClickListener {
                    val action = PhotoFragmentDirections.actionGlobalReportFragment(nickname = user.nickname)
                    findNavController().navigate(action)
                }
            }
        }

        binding.photoImgClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
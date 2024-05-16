package com.changs.android.gnuting_android.ui.fragment.user

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentPhotoBinding
import com.changs.android.gnuting_android.ui.HomeActivity
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
            if (user.id == null || user.nickname == null) {
                (requireActivity() as HomeActivity).showToast("탈퇴한 회원이거나 이용이 제한된 회원입니다.")
                findNavController().popBackStack()
            }


            Glide.with(requireContext()).load(user.profileImage)
                .error(R.drawable.ic_profile).into(binding.photoImg)

            binding.photoTxtName.text = user.nickname
            binding.photoTxtInfo.text = "${user.studentId} | ${user.department}"

            if ((viewModel.myInfo.value?.nickname) == user.nickname) {
                binding.photoTxtReport.visibility = View.INVISIBLE
            } else {
                binding.photoTxtReport.setOnClickListener {
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
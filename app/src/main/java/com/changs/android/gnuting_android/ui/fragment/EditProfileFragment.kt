package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.databinding.FragmentEditProflieBinding
import timber.log.Timber


class EditProfileFragment: BaseFragment<FragmentEditProflieBinding>(FragmentEditProflieBinding::bind, R.layout.fragment_edit_proflie) {
    private val args: EditProfileFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: 프로필 수정 API 연동

        args.member.let {
            binding.editProfileEditNickName.setText(it.nickname)
            binding.editProfileEditDepartment.setText(it.department)
            binding.editProfileEditIntro.setText(it.userSelfIntroduction)

            Glide.with(this).load(it.profileImage).error(R.drawable.ic_profile).into(binding.editProfileImg)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Timber.d("Selected URI: $uri")
                    Glide.with(this).load(uri).circleCrop().error(R.drawable.ic_profile).into(binding.editProfileImg)
                } else {
                    Timber.d("No media selected")
                }
            }

        binding.editProfileImg.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }
}
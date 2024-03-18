package com.changs.android.gnuting_android.ui.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.databinding.FragmentEditProflieBinding
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.getBitmap
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class EditProfileFragment : BaseFragment<FragmentEditProflieBinding>(
    FragmentEditProflieBinding::bind, R.layout.fragment_edit_proflie
) {
    private val args: EditProfileFragmentArgs by navArgs()
    private var preNickName: String? = null
    private val viewModel: HomeMainViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.member.let {
            binding.editProfileEditNickName.setText(it.nickname)
            preNickName = it.nickname
            viewModel.nickname = it.nickname
            binding.editProfileTxtMajor.text = it.department
            binding.editProfileEditIntro.setText(it.userSelfIntroduction)

            viewModel.department = it.department

            Glide.with(this).load(it.profileImage).error(R.drawable.ic_profile)
                .into(binding.editProfileImg)

            it.profileImage?.let { img ->
                Glide.with(this).asBitmap().load(img).into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap, transition: Transition<in Bitmap>?
                    ) {
                        viewModel.profileImage = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            }
        }
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.editProfileBtnConfirmation.setOnClickListener {
            viewModel.getCheckNickName()
        }

        binding.editProfileImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editProfileTxtEditProfile.setOnClickListener {
            if (preNickName != binding.editProfileEditNickName.text.toString()) {
                viewModel.nickNameCheck.value?.let {
                    if (it) {
                        viewModel.updateProfile(
                            department = binding.editProfileTxtMajor.text.toString(),
                            nickname = binding.editProfileEditNickName.text.toString(),
                            userSelfIntroduction = binding.editProfileEditIntro.text.toString()
                        )
                    } else {
                        Snackbar.make(binding.root, "닉네임 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                } ?: Snackbar.make(binding.root, "닉네임 인증이 완료되지 않았습니다.", Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.updateProfile(
                    department = binding.editProfileTxtMajor.text.toString(),
                    nickname = binding.editProfileEditNickName.text.toString(),
                    userSelfIntroduction = binding.editProfileEditIntro.text.toString()
                )
            }
        }

        binding.editProfileEditNickName.doAfterTextChanged {
            viewModel.nickname = it.toString()
        }

        binding.editProfileLlMajorContainer.setOnClickListener {
            val bottomDialogFragment = ProfileSearchDepartmentBottomSheetFragment()
            bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Timber.d("Selected URI: $uri")
                    viewModel.profileImage = uri.getBitmap(requireContext().contentResolver)
                    Glide.with(this).load(uri).circleCrop().error(R.drawable.ic_profile)
                        .into(binding.editProfileImg)
                } else {
                    Timber.d("No media selected")
                }
            }

        binding.editProfileImg.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setObserver() {
        viewModel.choiceDepartment.observe(viewLifecycleOwner) {
            viewModel.department = it
            binding.editProfileTxtMajor.text = it
        }

        viewModel.profileResponse.eventObserve(viewLifecycleOwner) {
            viewModel.fetchRecentMyInfo()
        }
    }
}
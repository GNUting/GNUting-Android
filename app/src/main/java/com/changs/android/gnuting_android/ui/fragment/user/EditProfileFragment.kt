package com.changs.android.gnuting_android.ui.fragment.user

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.databinding.FragmentEditProflieBinding
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.ProfileSearchDepartmentBottomSheetFragment
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.getBitmap
import com.changs.android.gnuting_android.util.setClickEvent
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProflieBinding>(
    FragmentEditProflieBinding::bind, R.layout.fragment_edit_proflie
) {
    private val args: EditProfileFragmentArgs by navArgs()
    private val viewModel: HomeMainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListener()
        setObserver()

        binding.editProfileEditNickName.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(10))
        binding.editProfileEditIntro.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))

        Glide.with(this).load(args.member.profileImage).error(R.drawable.ic_profile)
            .into(binding.editProfileImg)

        args.member.profileImage?.let { img ->
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

    override fun onResume() {
        super.onResume()
        args.member.let {
            binding.editProfileEditNickName.setText(it.nickname)
            binding.editProfileEditIntro.setText(it.userSelfIntroduction)

            viewModel.nickNameCheck.value = true
            viewModel.choiceDepartment.value = it.department
        }

    }

    private fun setListener() {
        binding.editProfileBtnConfirmation.setOnClickListener {
            viewModel.getCheckNickName(binding.editProfileEditNickName.text.toString())
        }

        binding.editProfileImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.editProfileTxtEditProfile.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            if (args.member.nickname != binding.editProfileEditNickName.text.toString()) {
                viewModel.nickNameCheck.value?.let {
                    if (it) {
                        viewModel.updateProfile(
                            department = binding.editProfileTxtMajor.text.toString(),
                            nickname = binding.editProfileEditNickName.text.toString(),
                            userSelfIntroduction = binding.editProfileEditIntro.text.toString()
                        )
                    } else {
                        Toast.makeText(requireContext(), "닉네임 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } ?: Toast.makeText(requireContext(), "닉네임 인증이 완료되지 않았습니다.", Toast.LENGTH_SHORT)
                    .show()

            } else {
                viewModel.updateProfile(
                    department = binding.editProfileTxtMajor.text.toString(),
                    nickname = binding.editProfileEditNickName.text.toString(),
                    userSelfIntroduction = binding.editProfileEditIntro.text.toString()
                )
            }
        }

        binding.editProfileEditNickName.doOnTextChanged { text, start, count, after ->
            if (!text.isNullOrEmpty() && args.member.nickname != binding.editProfileEditNickName.text.toString()) {
                binding.editProfileBtnConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.editProfileBtnConfirmation.isEnabled = true
                viewModel.nickNameCheck.value = false
            } else {
                binding.editProfileBtnConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.editProfileBtnConfirmation.isEnabled = false
                viewModel.nickNameCheck.value = true
            }
        }


        binding.editProfileLlMajorContainer.setOnClickListener {
            val bottomDialogFragment = ProfileSearchDepartmentBottomSheetFragment()
            bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
        }


        binding.editProfileImg.setOnClickListener {
            val dialog = Dialog(requireContext())

            dialog.setContentView(R.layout.dialog_edit_profile)

            val pickerBtn = dialog.findViewById<TextView>(R.id.dialog_edit_profile_txt_show_picker)
            val defaultImgBtn =
                dialog.findViewById<TextView>(R.id.dialog_edit_profile_txt_default_image)

            pickerBtn.setOnClickListener {
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
                        dialog.dismiss()
                    }

                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }

            defaultImgBtn.setOnClickListener {
                Glide.with(this).load(R.drawable.ic_profile).circleCrop()
                    .into(binding.editProfileImg)

                viewModel.profileImage = null
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    private fun setObserver() {
        viewModel.nickNameCheck.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.editProfileBtnConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                binding.editProfileBtnConfirmation.isEnabled = false
            } else {
                binding.editProfileBtnConfirmation.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                binding.editProfileBtnConfirmation.isEnabled = true
            }
        }

        viewModel.choiceDepartment.observe(viewLifecycleOwner) {
            binding.editProfileTxtMajor.text = it
        }

        viewModel.profileResponse.eventObserve(viewLifecycleOwner) {
            viewModel.fetchRecentMyInfo()
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with(viewModel) {
            nickNameCheck.value = null
            choiceDepartment.value = null
        }
    }
}
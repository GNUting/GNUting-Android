package com.changs.android.gnuting_android.ui.fragment.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.AddMemberBottomSheetFragment
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.CurrentMemberBottomSheetFragment
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class DetailFragment :
    BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::bind, R.layout.fragment_detail) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val memberAddViewModel: MemberAddViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostDetail(args.id)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.detailImgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.detailImgSetting.setOnClickListener {
            if (binding.detailLlSpinner.isVisible) {
                binding.detailLlSpinner.visibility = View.GONE
            } else {
                binding.detailLlSpinner.visibility = View.VISIBLE
            }
        }

        binding.detailTxtMenuEdit.setOnClickListener {
            val action = DetailFragmentDirections.actionDetailFragmentToEditPostFragment(
                args.id
            )
            findNavController().navigate(action)
        }

        binding.detailTxtMenuRemove.setOnClickListener {
            viewModel.deletePost(args.id)
        }

        binding.detailTxtMenuReportd.setOnClickListener {
            val action = DetailFragmentDirections.actionGlobalReportFragment(id = args.id)
            findNavController().navigate(action)
        }
    }

    private fun setObserver() {
        memberAddViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        viewModel.deletePostResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        viewModel.postDetailResponse.observe(viewLifecycleOwner) {
            it.result.apply {
                viewModel.myInfo.value?.let { myInfo ->
                    if (myInfo.nickname == user.nickname) {
                        binding.detailBtnChatRequest.visibility = View.GONE
                        binding.detailTxtMenuEdit.visibility = View.VISIBLE
                        binding.detailTxtMenuRemove.visibility = View.VISIBLE
                        binding.detailViewMenuLine1.visibility = View.VISIBLE
                        binding.detailViewMenuLine2.visibility = View.VISIBLE
                    }
                }

                Glide.with(this@DetailFragment).load(user.profileImage).error(R.drawable.ic_profile)
                    .into(binding.detailImgProfile)

                binding.detailImgProfile.setOnClickListener {
                    val inUser = InUser(
                        age = "",
                        department = "",
                        gender = "",
                        nickname = user.nickname,
                        id = -1,
                        profileImage = user.profileImage,
                        studentId = "",
                        userRole = "",
                        userSelfIntroduction = ""
                    )
                    val args = bundleOf("user" to inUser)
                    findNavController().navigate(R.id.photoFragment, args)
                }


                binding.detailTxtPostTitle.text = title
                binding.detailTxtNickname.text = user.nickname
                binding.detailTxtInfo.text = "${user.department} | ${user.studentId}"
                binding.detailTxtDetail.text = detail
                binding.detailTxtCurrentParticipant.text = "과팅 멤버 정보 ${inUser.size}명"
                binding.detailTxtTime.text = time

                binding.detailTxtCurrentParticipant.setOnClickListener {
                    val bottomDialogFragment = CurrentMemberBottomSheetFragment(inUser)
                    bottomDialogFragment.show(childFragmentManager, bottomDialogFragment.tag)
                }

                if (status != "OPEN") {
                    binding.detailBtnChatRequest.isEnabled = false
                    // TODO: 비활성화 색상 표시
                }

                binding.detailBtnChatRequest.setOnClickListener {
                    AddMemberBottomSheetFragment(memberAddViewModel, args.id).show(
                        childFragmentManager, null
                    )
                }
            }
        }
    }

}
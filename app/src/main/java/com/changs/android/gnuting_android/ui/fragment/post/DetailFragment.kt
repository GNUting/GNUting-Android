package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.ApplicationAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.showTwoButtonDialog
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class DetailFragment :
    BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::bind, R.layout.fragment_detail) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val args: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postViewModel.getPostDetail(args.id)
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.detailCl.setOnClickListener {
            binding.detailLlSpinner.visibility = View.GONE
        }

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

        binding.detailTxtMenuRemove.setOnClickListener {
            showTwoButtonDialog(requireContext(), "정말로 삭제하시겠습니까?", rightButtonText = "삭제") {
                postViewModel.deletePost(args.id)
            }
        }

        binding.detailTxtMenuReport.setOnClickListener {
            val action = DetailFragmentDirections.actionGlobalReportFragment(id = args.id)
            findNavController().navigate(action)
        }
    }

    private fun setObserver() {
        postViewModel.spinner.observe(viewLifecycleOwner) { show ->
            binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
        }

        postViewModel.toast.eventObserve(viewLifecycleOwner) { text ->
            text?.let {
                (requireActivity() as HomeActivity).showToast(it)
            }
        }

        postViewModel.deletePostResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
        postViewModel.postDetailResponse.observe(viewLifecycleOwner) { response ->
            binding.detailTxtMenuEdit.setOnClickListener {
                val bundle = bundleOf("id" to args.id, "detail" to response.result)
                findNavController().navigate(R.id.action_detailFragment_to_editPostFragment, bundle)
            }


            response.result.apply {
                viewModel.myInfo.value?.let { myInfo ->
                    if (myInfo.nickname == user.nickname) {
                        binding.detailTxtTitle.text = "내가 쓴 게시글"
                        binding.detailTxtMenuEdit.visibility = View.VISIBLE
                        binding.detailTxtMenuRemove.visibility = View.VISIBLE
                        binding.detailViewMenuLine1.visibility = View.VISIBLE

                        binding.detailBtnChatRequest.visibility = View.GONE
                        binding.detailTxtCurrentParticipant.visibility = View.GONE

                        binding.detailBtnApplication.visibility = View.VISIBLE

                        binding.detailBtnApplication.setOnClickListener {
                            viewModel.currentApplicationTab =
                                ApplicationAdapter.ApplicationType.PARTICIPANT
                            findNavController().navigate(R.id.listFragment2)
                        }
                    } else {
                        binding.detailTxtTitle.text = "과팅 게시판"
                        binding.detailBtnChatRequest.visibility = View.VISIBLE
                        binding.detailTxtMenuReport.visibility = View.VISIBLE

                        binding.detailTxtCurrentParticipant.text = "과팅 멤버 정보 ${inUser.size}명"
                        binding.detailTxtCurrentParticipant.visibility = View.VISIBLE
                    }
                }

                Glide.with(this@DetailFragment).load(user.profileImage).error(R.drawable.ic_profile)
                    .into(binding.detailImgProfile)

                binding.detailImgProfile.setOnClickListener {
                    val inUser = InUser(
                        age = "",
                        department = user.department,
                        gender = "",
                        nickname = user.nickname,
                        id = -1,
                        profileImage = user.profileImage,
                        studentId = user.studentId,
                        userRole = "",
                        userSelfIntroduction = ""
                    )
                    val args = bundleOf("user" to inUser)
                    findNavController().navigate(R.id.action_detailFragment_to_photoFragment3, args)
                }


                binding.detailTxtPostTitle.text = title
                binding.detailTxtNickname.text = user.nickname
                binding.detailTxtInfo.text = "${user.department} | ${user.studentId}"
                binding.detailTxtDetail.text = detail
                binding.detailTxtTime.text = time

                binding.detailTxtCurrentParticipant.setOnClickListener {
                    val bundle = bundleOf("currentMember" to inUser.toTypedArray())
                    findNavController().navigate(
                        R.id.action_detailFragment_to_currentMemberBottomSheetFragment, bundle
                    )
                }

                if (status != "OPEN") {
                    binding.detailBtnChatRequest.isEnabled = false
                    binding.detailBtnChatRequest.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                }

                binding.detailBtnChatRequest.setOnClickListener {
                    val bundle = bundleOf("boardId" to args.id)
                    findNavController().navigate(
                        R.id.action_detailFragment_to_addMemberBottomSheetFragment, bundle
                    )
                }
            }
        }
    }

}
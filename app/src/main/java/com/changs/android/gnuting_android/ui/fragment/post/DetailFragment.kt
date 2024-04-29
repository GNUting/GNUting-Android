package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.FragmentDetailBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.AddMemberBottomSheetFragment
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.CurrentMemberBottomSheetFragment
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.util.hideSoftKeyboard
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
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
            postViewModel.deletePost(args.id)
        }

        binding.detailTxtMenuReportd.setOnClickListener {
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
                        binding.detailBtnChatRequest.visibility = View.GONE
                        binding.detailTxtMenuEdit.visibility = View.VISIBLE
                        binding.detailTxtMenuRemove.visibility = View.VISIBLE
                        binding.detailViewMenuLine1.visibility = View.VISIBLE
                        binding.detailViewMenuLine2.visibility = View.VISIBLE
                    } else {
                        binding.detailBtnChatRequest.visibility = View.VISIBLE
                    }
                }

                Glide.with(this@DetailFragment).load(user.profileImage)
                    .error(R.drawable.ic_profile)
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
                binding.detailTxtCurrentParticipant.text = "과팅 멤버 정보 ${inUser.size}명"
                binding.detailTxtCurrentParticipant.visibility = View.VISIBLE
                binding.detailTxtTime.text = time

                binding.detailTxtCurrentParticipant.setOnClickListener {
                    val bundle = bundleOf("currentMember" to inUser.toTypedArray())
                    findNavController().navigate(R.id.action_detailFragment_to_currentMemberBottomSheetFragment, bundle)
                }

                if (status != "OPEN") {
                    binding.detailBtnChatRequest.isEnabled = false
                    binding.detailBtnChatRequest.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                }

                binding.detailBtnChatRequest.setOnClickListener {
                    val bundle = bundleOf("boardId" to args.id)
                    findNavController().navigate(R.id.action_detailFragment_to_addMemberBottomSheetFragment, bundle)
                }
            }
        }
    }

}
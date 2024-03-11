package com.changs.android.gnuting_android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.databinding.FragmentEditPostBinding
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class EditPostFragment : BaseFragment<FragmentEditPostBinding>(
    FragmentEditPostBinding::bind, R.layout.fragment_edit_post
) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val memberAddViewModel: MemberAddViewModel by viewModels() { MemberAddViewModel.Factory }
    private lateinit var adapter: PostMemberAdapter
    private val args: EditPostFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPostDetail(args.id)
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.editPostImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.editPostLlAddMember.setOnClickListener {
            val searchMemberBottomSheetFragment =
                SearchMemberBottomSheetFragment(memberAddViewModel)
            searchMemberBottomSheetFragment.show(childFragmentManager, null)
        }

        binding.editPostTxtComplete.setOnClickListener {
            if (!binding.editPostEditTitle.text.isNullOrEmpty() && !binding.editPostEditDetail.text.isNullOrEmpty()) {
                if ((memberAddViewModel.currentMember.value?.size ?: 0) != 0) {
                    val request = SaveRequest(
                        detail = binding.editPostEditDetail.text.toString(),
                        title = binding.editPostEditTitle.text.toString(),
                        inUser = memberAddViewModel.currentMember.value!!.toList()
                    )
                    viewModel.patchSave(args.id, request)
                }
            } else {
                Snackbar.make(binding.root, "게시글 작성을 완료해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerView() {
        adapter = PostMemberAdapter()
        binding.editPostRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        viewModel.postDetailResponse.observe(viewLifecycleOwner) {
            it.result.apply {
                binding.editPostEditTitle.setText(title)
                binding.editPostEditDetail.setText(detail)
                memberAddViewModel.currentMember.value = inUser.toMutableList()
            }
        }

        memberAddViewModel.currentMember.observe(viewLifecycleOwner) {
            binding.editPostTxtMemberTitle.text = "멤버 (${it.size})"
            adapter.submitList(it)
        }

        viewModel.myInfo.value?.let { myInfo ->
            val myUserInfo = InUser(
                age = myInfo.age,
                department = myInfo.department,
                gender = myInfo.gender,
                id = myInfo.id,
                nickname = myInfo.nickname,
                profileImage = myInfo.profileImage,
                studentId = myInfo.studentId,
                userRole = myInfo.userRole,
                userSelfIntroduction = myInfo.userSelfIntroduction
            )

            memberAddViewModel.currentMember.value = mutableListOf(myUserInfo)
        }

        viewModel.patchPostDetailResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }
}
package com.changs.android.gnuting_android.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.GNUApplication
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.databinding.FragmentPostBinding
import com.changs.android.gnuting_android.ui.MainActivity
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class PostFragment :
    BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind, R.layout.fragment_post) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val memberAddViewModel: MemberAddViewModel by viewModels() { MemberAddViewModel.Factory }
    private lateinit var adapter: PostMemberAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.postImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postLlAddMember.setOnClickListener {
            val searchMemberBottomSheetFragment =
                SearchMemberBottomSheetFragment(memberAddViewModel)
            searchMemberBottomSheetFragment.show(childFragmentManager, null)
        }

        binding.postTxtComplete.setOnClickListener {
            if (!binding.postEditTitle.text.isNullOrEmpty() && !binding.postEditDetail.text.isNullOrEmpty()) {
                if ((memberAddViewModel.currentMember.value?.size ?: 0) != 0) {
                    val request = SaveRequest(
                        detail = binding.postEditDetail.text.toString(),
                        title = binding.postEditTitle.text.toString(),
                        inUser = memberAddViewModel.currentMember.value!!.toList()
                    )
                    viewModel.postSave(request)
                }
            } else {
                Snackbar.make(binding.root, "게시글 작성을 완료해주세요.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerView() {
        adapter = PostMemberAdapter()
        binding.postRecyclerview.adapter = adapter
    }

    private fun setObserver() {
        memberAddViewModel.currentMember.observe(viewLifecycleOwner) {
            binding.postTxtMemberTitle.text = "멤버 (${it.size})"
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

        viewModel.saveResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        memberAddViewModel.expirationToken.eventObserve(viewLifecycleOwner) {
            GNUApplication.sharedPreferences.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
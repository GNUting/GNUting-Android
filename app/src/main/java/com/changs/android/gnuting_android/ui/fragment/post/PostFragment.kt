package com.changs.android.gnuting_android.ui.fragment.post

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.base.BaseFragment
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.databinding.FragmentPostBinding
import com.changs.android.gnuting_android.ui.HomeActivity
import com.changs.android.gnuting_android.ui.adapter.PostMemberAdapter
import com.changs.android.gnuting_android.ui.fragment.bottomsheet.SearchMemberBottomSheetFragment
import com.changs.android.gnuting_android.util.eventObserve
import com.changs.android.gnuting_android.viewmodel.HomeMainViewModel
import com.changs.android.gnuting_android.viewmodel.MemberAddViewModel
import com.changs.android.gnuting_android.viewmodel.PostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class PostFragment :
    BaseFragment<FragmentPostBinding>(FragmentPostBinding::bind, R.layout.fragment_post) {
    private val viewModel: HomeMainViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels()
    private val memberAddViewModel: MemberAddViewModel by hiltNavGraphViewModels(R.id.post_graph)
    private lateinit var adapter: PostMemberAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setListener()
        setObserver()
    }

    private fun setListener() {
        binding.postEditTitle.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(30))
        binding.postEditDetail.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(300))
        binding.postImgBack.setOnClickListener { findNavController().popBackStack() }

        binding.postLlAddMember.setOnClickListener {
            findNavController().navigate(R.id.action_postFragment_to_postSearchMemberBottomSheetFragment)
        }

        binding.postTxtComplete.setOnClickListener {
            if (!binding.postEditTitle.text.isNullOrEmpty() && !binding.postEditDetail.text.isNullOrEmpty()) {
                if ((memberAddViewModel.currentMember.value?.size ?: 0) != 0) {
                    val request = SaveRequest(
                        detail = binding.postEditDetail.text.toString(),
                        title = binding.postEditTitle.text.toString(),
                        inUser = memberAddViewModel.currentMember.value!!.toList()
                    )
                    postViewModel.postSave(request)
                }
            } else {
                (requireActivity() as HomeActivity).showToast("게시글 작성을 완료해주세요.")
            }
        }
    }

    private fun setRecyclerView() {
        adapter = PostMemberAdapter(::navigateListener)
        binding.postRecyclerview.adapter = adapter
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

            if (memberAddViewModel.currentMember.value == null) {
                memberAddViewModel.currentMember.value = mutableListOf(myUserInfo)
            }
        }

        postViewModel.saveResponse.eventObserve(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun navigateListener(user: InUser) {
        val args = bundleOf("user" to user)
        findNavController().navigate(R.id.action_postFragment_to_photoFragment2, args)
    }
}